# Roadmap

Ideas and known issues for OLED Saver for Android TV. Items are grouped by rough
effort so it is easy to pick something that fits the time available. Move an item
to the "Done" section when it ships, ideally with the commit or PR reference.

Effort guide:

- **Low**: an hour or two, one or two files, no new concepts.
- **Medium**: a day or so, touches several layers (settings, UI, alarm/config).
- **High**: multi-day, architectural change or research needed.

## Bugs

Fix these before new features; each one changes current behaviour silently.

- [ ] **Stale "started today" flag blocks masking for the rest of the day.** The flag is a
      persisted date written when the masker launches and cleared only by a dismiss. If the
      masker dies without one (system kill, force-stop, Home + eviction) every later launch
      reads "already started" and finishes. Observed on the phone emulator on 2026-10-28:
      a force-stop at 12:04 left the flag set and no heartbeat showed the masker afterwards.
      Fix: replace the persisted date with a process-local "masker alive" flag (set in
      `MaskerActivity.onCreate`, cleared in `onDestroy`); a killed process takes it along, so
      the next launch recovers by itself. The cron-spam guard keeps working because repeated
      launches land in the same live process.
- [ ] **`BootBroadcastReceiverTest` leaks device state.** The receiver launches a real
      `MainActivity`, which under the emulator `now` override starts the masker and rewrites
      the started flag after the test's cleanup has run. Seed `disabled_until` a few minutes
      ahead in `@Before` so the launched activity takes the Disabled branch and writes
      nothing, and clear both fields in `@After`.
- [ ] **Restored task shows the snooze screen instead of the masks.** Pressing Back
      pushes `ExitFragment` onto the nav back stack. If the activity then goes to the
      background (Home, input switch) the task is parked in that state, and every
      restore path (launcher, recents, reorder-to-front) shows it as left, because the
      only `navigateToMasker()` call lives in `onCreate`. Fix: in `MaskerActivity`,
      pop back to the masker in `onStop` when the exit screen is showing, navigate to
      the masker in `onNewIntent`, and derive `exitFragmentDisplayed` from the current
      nav destination (or save it) so a process-restore does not open a second exit
      screen on the next Back.
- [ ] **Dead / duplicated work.** `ExitFragment.dismiss()` launches an empty
      coroutine; `MaskingFragmentPolsat` restarts all animations on every visibility
      change, stacking infinite animators.

## Low effort

- [ ] **Show up in the TV launcher.** Add `android.intent.category.LEANBACK_LAUNCHER`,
      `<uses-feature android:name="android.software.leanback" />` and a banner
      drawable. Today the app only has a phone launcher entry.
- [ ] **Auto-stop at the end of the operating window.** `MaskerActivity` never checks
      the range end, so once started it stays until snoozed. Schedule a finish at the
      upper bound of `getOperatingRange()`.
- [ ] **Move user-facing text to string resources.** Snooze button labels, the toast
      and `AlarmDelay.message` are hard-coded Polish. Unblocks an English variant for
      a store listing.
- [ ] **Exit screen timeout.** If no snooze option is chosen within ~30 s, return to
      the masker automatically. Covers an accidental Back on the remote and keeps the
      task from being parked on the exit screen (see the restore bug above).
- [ ] **On-screen status line.** Current channel name, time until the window ends and
      a snooze hint. The `MaskingTitle` view already exists; mostly wiring.

## Medium effort

- [ ] **Settings screen.** Operating hours, days of week and default channel, stored in
      the proto `Settings` message and read by `ConfigProvider` instead of constants.
- [ ] **Per-element toggles for every channel.** Only the bold stripe can be hidden,
      only in the TVN fragment, and the state is lost on restart. Generalise
      `MaskerVisibilityRequest` to a set of element ids and persist it per channel.
- [ ] **Calibration mode.** D-pad moves and resizes the focused mask; offsets are saved
      per channel. Replaces the current edit-rebuild-deploy loop for tuning positions.
- [ ] **Multiple windows per day.** For example a morning and an evening news slot.
      Needs a repeated range field in the proto and a small change to
      `MaskingAlarmManager.getNextDayExecutionTimeMillis()`.
- [ ] **`distinctUntilChanged()` on repository flows.** `started` and `maskIndex` are
      derived from the whole `Settings` object, so any write wakes every collector.

## High effort

- [ ] **Real overlay window instead of a translucent activity.** A foreground service
      drawing with `TYPE_APPLICATION_OVERLAY` survives the TV app taking focus, lets
      remote presses pass through, and removes the dependence on background activity
      start rules that tighten with every Android release. The overlay permission is
      already requested.
- [ ] **Channel profile editor.** Replace one fragment plus one layout per channel with
      a data-driven list of masks (shape, position, size) defined on the TV and stored
      in the proto. Adding a channel becomes a settings action, not a code change.
- [ ] **Automatic channel detection.** Pick the mask set from the current channel via
      the TV Input Framework where the device exposes it. Research feasibility on the
      target TV first.

## Done

- [x] **Split into `:core` (pure Kotlin/JVM) and `:app` (Android).** Operating hours, launch
      decision, alarm timing and snooze delays live in `:core` with JVM unit tests and build
      with Maven Central only; `:app` is included only when an Android SDK is configured.
      While moving, `wasStartedToday` now compares full dates in Europe/Warsaw (fixes the
      day-of-month bug). _(working tree, not yet committed)_
- [x] **Operating hours overridable for emulator testing.** `OperatingHours` holds the
      07:10-12:00 default; `oledsaver.operatingHours=now` (or `HH:mm-HH:mm`) in
      `local.properties` is compiled into debug builds and applied only on an emulator.
      Groundwork for the user-facing settings screen. _(working tree, not yet committed)_
- [x] **Boot autostart never fired.** `BootBroadcastReceiver` compared the action against
      the bare string `"BOOT_COMPLETED"`. Now matches `Intent.ACTION_BOOT_COMPLETED` and
      `QUICKBOOT_POWERON`, and clears a stale "started today" flag before launching
      MainActivity so a reboot during masking does not block the rest of the day.
      Covered by `BootBroadcastReceiverTest`. _(working tree, not yet committed)_
- [x] **Snooze survives relaunches (`disabled_until`).** The dismiss deadline used to live
      only in AlarmManager; any MainActivity launch replaced it with the one-minute
      heartbeat and, inside the window, showed the masker again. Now persisted in the
      proto `Settings`, honoured by `ConfigProvider.decide()`, the alarm is re-armed at
      the deadline, and a launch during the window shows a toast "Wygaszacz wyłączony do
      HH:mm". Covered by `DisabledUntilTest` and `MainActivityDisabledTest`.
      _(working tree, not yet committed)_
- [x] **`settings.started.collect` re-entry in `MainActivity`.** Replaced with one-shot
      `first()` reads as part of the change above. _(working tree, not yet committed)_
