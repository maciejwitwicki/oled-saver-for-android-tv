#!/usr/bin/env bash
# Prepare an offline dependency bundle so the project builds in a sandbox that cannot reach
# Google Maven (dl.google.com) or the Android SDK download site.
#
# Run this on a machine WITH internet access:
#     sandbox/prepare-offline.sh [--target-os linux] [--target-arch x86_64] [--skip-verify]
#
# Result: <project>/offline/ (gitignored)
#     m2/        Maven-layout mirror of every artifact the build needs (plugins included)
#     sdk/       Android SDK platform + build-tools for the TARGET OS + licences
#     MANIFEST   what was bundled, from which build files, when
#
# Then copy/zip the whole project directory (including offline/) into the sandbox.
# settings.gradle detects offline/m2 and, when remote repositories are unreachable, uses
# the bundle instead (force with -Poffline=true or OLED_OFFLINE=1).
#
# Safety: everything is assembled in a mktemp staging directory and moved into place.
# The only deletions go through remove_dir(), which refuses any path that is not a
# directory we created under the temp dir, or a previous offline/ dir under the project root.
set -euo pipefail

die() { printf 'error: %s\n' "$*" >&2; exit 1; }
log() { printf '\n==> %s\n' "$*"; }

TARGET_OS=linux
TARGET_ARCH=x86_64
VERIFY=1
while [[ $# -gt 0 ]]; do
  case "$1" in
    --target-os)   [[ $# -ge 2 ]] || die "--target-os needs a value";   TARGET_OS="$2";   shift 2 ;;
    --target-arch) [[ $# -ge 2 ]] || die "--target-arch needs a value"; TARGET_ARCH="$2"; shift 2 ;;
    --skip-verify) VERIFY=0; shift ;;
    -h|--help) sed -n '2,20p' "$0"; exit 0 ;;
    *) die "unknown option: $1" ;;
  esac
done

# ---- locate and validate the project root (never trust a derived path blindly) ----------
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd -P)" || die "cannot resolve project root"
[[ -n "$ROOT" && "$ROOT" != "/" && -f "$ROOT/settings.gradle" && -d "$ROOT/app" && -d "$ROOT/core" ]] \
  || die "project root not recognised: '$ROOT'"
OUT="$ROOT/offline"
WORK="$(mktemp -d "${TMPDIR:-/tmp}/oled-offline.XXXXXX")" || die "mktemp failed"
[[ -n "$WORK" && -d "$WORK" && "$WORK" == */oled-offline.* ]] || die "unexpected temp dir '$WORK'"
STAGE="$WORK/offline"
GRADLEW="sh $ROOT/gradlew"

# Guarded delete: only directories we own, never empty, never '/', never a symlink.
remove_dir() {
  local p="${1:-}"
  [[ -n "$p" ]]                 || die "remove_dir: empty path"
  [[ "$p" == /* && "$p" != "/" ]] || die "remove_dir: refusing '$p'"
  [[ -d "$p" && ! -L "$p" ]]    || die "remove_dir: not a plain directory: '$p'"
  case "$p" in
    "$WORK"|"$WORK"/*) ;;                       # our temp area
    "$ROOT"/offline.old.*) ;;                   # a previous bundle we moved aside
    *) die "remove_dir: '$p' is outside the allowed locations" ;;
  esac
  rm -rf -- "$p"
}
cleanup() { [[ -d "${WORK:-}" ]] && remove_dir "$WORK" || true; }
trap cleanup EXIT

# ---- versions pinned by the build files -------------------------------------------------
COMPILE_SDK=$(sed -nE 's/^[[:space:]]*compileSdk[[:space:]]*=?[[:space:]]*([0-9]+).*/\1/p' "$ROOT/app/build.gradle" | head -1)
BUILD_TOOLS=$(sed -nE "s/^[[:space:]]*buildToolsVersion[[:space:]]*['\"]([0-9.]+)['\"].*/\1/p" "$ROOT/app/build.gradle" | head -1)
PROTOC_VERSION=$(sed -nE 's/.*com\.google\.protobuf:protoc:([0-9.]+).*/\1/p' "$ROOT/app/build.gradle" | head -1)
[[ -n "$COMPILE_SDK" && -n "$BUILD_TOOLS" && -n "$PROTOC_VERSION" ]] \
  || die "could not read compileSdk / buildToolsVersion / protoc version from app/build.gradle"
log "project=$ROOT"
log "target=$TARGET_OS/$TARGET_ARCH  compileSdk=$COMPILE_SDK  build-tools=$BUILD_TOOLS  protoc=$PROTOC_VERSION"
mkdir -p "$STAGE/m2" "$STAGE/sdk/platforms" "$STAGE/sdk/build-tools" "$STAGE/sdk/licenses"

# ------------------------------------------------------------------------------------------
# 1. Seed an EMPTY Gradle home by running the real build online -> exact dependency closure.
#    The already downloaded Gradle distribution is copied in so the wrapper does not refetch it.
# ------------------------------------------------------------------------------------------
SEED="$WORK/gradle-home-seed"
log "1/6 seeding a fresh Gradle home at $SEED (downloads everything the build needs)"
mkdir -p "$SEED/wrapper"
if [[ -d "$HOME/.gradle/wrapper/dists" ]]; then cp -R "$HOME/.gradle/wrapper/dists" "$SEED/wrapper/dists"; fi
( cd "$ROOT" && GRADLE_USER_HOME="$SEED" $GRADLEW -Poffline=false -PincludeAndroidApp=true \
    --no-configuration-cache --no-daemon --console=plain -q \
    :core:build :app:assembleDebug :app:testDebugUnitTest :app:assembleDebugAndroidTest )

# ------------------------------------------------------------------------------------------
# 2. Add host-specific artifacts for the TARGET (the seed only has this machine's variants):
#    aapt2 (Android Gradle Plugin) and protoc (protobuf plugin).
# ------------------------------------------------------------------------------------------
log "2/6 resolving $TARGET_OS-specific artifacts (aapt2, protoc)"
AAPT2_VERSION=$(find "$SEED/caches/modules-2/files-2.1/com.android.tools.build/aapt2" -mindepth 1 -maxdepth 1 -type d -exec basename {} \; | head -1)
[[ -n "$AAPT2_VERSION" ]] || die "aapt2 not found in the seed cache"
AAPT2_CLASSIFIER="$TARGET_OS"
[[ "$TARGET_OS" == "linux" && "$TARGET_ARCH" != "x86_64" ]] && AAPT2_CLASSIFIER="linux-$TARGET_ARCH"
PROTOC_CLASSIFIER="${TARGET_OS}-${TARGET_ARCH}"
[[ "$TARGET_ARCH" == "aarch64" || "$TARGET_ARCH" == "arm64" ]] && PROTOC_CLASSIFIER="${TARGET_OS}-aarch_64"
mkdir -p "$WORK/extras"
printf "rootProject.name = 'offline-extras'\n" > "$WORK/extras/settings.gradle"
cat > "$WORK/extras/build.gradle" <<EOS
repositories { google(); mavenCentral() }
def wanted = [
    aapt2:  "com.android.tools.build:aapt2:${AAPT2_VERSION}:${AAPT2_CLASSIFIER}",
    protoc: "com.google.protobuf:protoc:${PROTOC_VERSION}:${PROTOC_CLASSIFIER}@exe",
]
wanted.each { name, coords ->
    configurations.create(name) { transitive = false }
    dependencies.add(name, coords)
}
tasks.register('resolveExtras') {
    doLast {
        wanted.each { name, coords ->
            try { println "resolved \${coords}: " + configurations.getByName(name).files*.name }
            catch (Exception e) { throw new GradleException("could not resolve \${coords}: \${e.message}") }
        }
    }
}
EOS
( cd "$WORK/extras" && GRADLE_USER_HOME="$SEED" $GRADLEW --no-configuration-cache --no-daemon --console=plain -q resolveExtras )

# ------------------------------------------------------------------------------------------
# 3. Flatten the Gradle module cache into a Maven-layout repository (in the staging dir).
# ------------------------------------------------------------------------------------------
log "3/6 flattening the cache into m2/"
python3 - "$SEED/caches/modules-2/files-2.1" "$STAGE/m2" <<'EOP'
import json, os, shutil, sys
src, dst = sys.argv[1], sys.argv[2]
n = 0
for group in sorted(os.listdir(src)):
    gdir = os.path.join(src, group)
    if not os.path.isdir(gdir): continue
    for module in os.listdir(gdir):
        for version in os.listdir(os.path.join(gdir, module)):
            vdir = os.path.join(gdir, module, version)
            for sha in os.listdir(vdir):
                sdir = os.path.join(vdir, sha)
                if not os.path.isdir(sdir): continue
                target_dir = os.path.join(dst, group.replace('.', '/'), module, version)
                os.makedirs(target_dir, exist_ok=True)
                for f in os.listdir(sdir):
                    shutil.copy2(os.path.join(sdir, f), os.path.join(target_dir, f))
                    n += 1
# Gradle caches artifacts under the module-metadata "name" (e.g. foo-release.aar) but a
# repository serves them under "url" (e.g. foo-android-1.0.aar). Add the url-named copies.
aliased = 0
for root_dir, _, files in os.walk(dst):
    for f in files:
        if not f.endswith('.module'): continue
        try:
            meta = json.load(open(os.path.join(root_dir, f)))
        except Exception:
            continue
        for variant in meta.get('variants', []):
            for entry in variant.get('files', []):
                name, url = entry.get('name'), entry.get('url')
                if not name or not url or name == url: continue
                src_file = os.path.join(root_dir, name)
                dst_file = os.path.join(root_dir, url)
                if os.path.isfile(src_file) and not os.path.exists(dst_file):
                    os.makedirs(os.path.dirname(dst_file), exist_ok=True)
                    shutil.copy2(src_file, dst_file)
                    aliased += 1
print(f"copied {n} files, added {aliased} url-named aliases from module metadata")
EOP

# ------------------------------------------------------------------------------------------
# 4. Android SDK pieces for the TARGET OS, from Google's repository index.
# ------------------------------------------------------------------------------------------
log "4/6 assembling sdk/ (platform android-$COMPILE_SDK, build-tools $BUILD_TOOLS for $TARGET_OS)"
REPO_BASE="https://dl.google.com/android/repository/"
curl -fsSL "${REPO_BASE}repository2-3.xml" -o "$WORK/repository.xml"
python3 - "$WORK/repository.xml" "$COMPILE_SDK" "$BUILD_TOOLS" "$TARGET_OS" > "$WORK/sdk-urls.txt" <<'EOP'
import sys, xml.etree.ElementTree as ET
xml_path, compile_sdk, build_tools, target_os = sys.argv[1:5]
root = ET.parse(xml_path).getroot()
def find(path_value, host_os=None):
    for pkg in root.iter('remotePackage'):
        if pkg.get('path') != path_value: continue
        for arch in pkg.iter('archive'):
            os_el = arch.find('host-os')
            if host_os and (os_el is None or os_el.text != host_os): continue
            if not host_os and os_el is not None: continue
            return arch.find('complete/url').text
    raise SystemExit(f"package {path_value} ({host_os or 'any os'}) not found in repository index")
print("platform", find(f"platforms;android-{compile_sdk}"))
print("build-tools", find(f"build-tools;{build_tools}", target_os))
EOP
while read -r kind url; do
  [[ -n "$kind" && -n "$url" ]] || die "bad entry in sdk-urls.txt"
  log "   downloading $kind: $url"
  mkdir -p "$WORK/dl/$kind"
  curl -fL --progress-bar "${REPO_BASE}${url}" -o "$WORK/dl/$kind.zip"
  unzip -q "$WORK/dl/$kind.zip" -d "$WORK/dl/$kind"
  top="$(find "$WORK/dl/$kind" -mindepth 1 -maxdepth 1 -type d | head -1)"
  [[ -n "$top" ]] || die "unexpected archive layout for $kind"
  case "$kind" in
    platform)    mv "$top" "$STAGE/sdk/platforms/android-$COMPILE_SDK" ;;
    build-tools) mv "$top" "$STAGE/sdk/build-tools/$BUILD_TOOLS" ;;
    *) die "unknown sdk piece '$kind'" ;;
  esac
done < "$WORK/sdk-urls.txt"
# Licence acceptance files (what `sdkmanager --licenses` writes); harmless when present.
for lic_dir in "${ANDROID_HOME:-}/licenses" "$HOME/Library/Android/sdk/licenses"; do
  [[ -d "$lic_dir" ]] && cp "$lic_dir"/* "$STAGE/sdk/licenses/" 2>/dev/null || true
done
[[ -n "$(ls -A "$STAGE/sdk/licenses")" ]] \
  || printf '\n8933bad161af4178b1185d1a37fbf41ea5269c55\nd56f5187479451eabf01fb78af6dfcb131a6481e\n24333f8a63b6825ea9c5514f83c2829b004d1fee\n' > "$STAGE/sdk/licenses/android-sdk-license"

# ------------------------------------------------------------------------------------------
# 5. Manifest, then move the staged bundle into place (previous bundle is moved aside first).
# ------------------------------------------------------------------------------------------
log "5/6 writing manifest and installing the bundle at $OUT"
BUILD_FILES_HASH=$(cat "$ROOT/settings.gradle" "$ROOT/build.gradle" "$ROOT/app/build.gradle" "$ROOT/core/build.gradle" "$ROOT/gradle/libs.versions.toml" | shasum -a 256 | cut -c1-16)
{
  echo "created=$(date -u +%Y-%m-%dT%H:%M:%SZ)"
  echo "git=$(git -C "$ROOT" rev-parse --short HEAD 2>/dev/null || echo unknown)"
  echo "buildFilesHash=$BUILD_FILES_HASH"
  echo "target=$TARGET_OS/$TARGET_ARCH"
  echo "compileSdk=$COMPILE_SDK buildTools=$BUILD_TOOLS aapt2=$AAPT2_VERSION:$AAPT2_CLASSIFIER protoc=$PROTOC_VERSION:$PROTOC_CLASSIFIER"
  echo "m2Files=$(find "$STAGE/m2" -type f | wc -l | tr -d ' ') m2Size=$(du -sh "$STAGE/m2" | cut -f1) sdkSize=$(du -sh "$STAGE/sdk" | cut -f1)"
  echo "sandboxUsage: gradle build   (or -Poffline=true to force; Gradle >= 8.13, JDK 17+ for :core, JDK 21 for :app)"
} > "$STAGE/MANIFEST"
cat "$STAGE/MANIFEST"
OLD=""
if [[ -e "$OUT" ]]; then
  OLD="$ROOT/offline.old.$(date +%Y%m%d%H%M%S)"
  mv "$OUT" "$OLD"
  log "   previous bundle moved to $OLD"
fi
mv "$STAGE" "$OUT"

# ------------------------------------------------------------------------------------------
# 6. Self-check: another EMPTY Gradle home, Gradle --offline, bundle as the only repository.
#    Proves the mirror is complete for this host; the target-OS binaries were resolved by name.
# ------------------------------------------------------------------------------------------
if [[ $VERIFY -eq 1 ]]; then
  log "6/6 self-check: building from the bundle only (fresh Gradle home, --offline)"
  VERIFY_HOME="$WORK/gradle-home-verify"
  mkdir -p "$VERIFY_HOME/wrapper" && cp -R "$SEED/wrapper/dists" "$VERIFY_HOME/wrapper/dists"
  if ( cd "$ROOT" && GRADLE_USER_HOME="$VERIFY_HOME" $GRADLEW --offline -Poffline=true -PincludeAndroidApp=true \
        --no-configuration-cache --no-daemon --console=plain -q :core:test :app:testDebugUnitTest :app:assembleDebug ); then
    echo "self-check PASSED"
  else
    die "self-check FAILED - bundle left at $OUT for inspection"
  fi
else
  log "6/6 self-check skipped"
fi

if [[ -n "$OLD" ]]; then remove_dir "$OLD"; log "   removed previous bundle $OLD"; fi
log "done: $OUT ready ($(du -sh "$OUT" | cut -f1)). Zip the project directory including offline/ for the sandbox."
