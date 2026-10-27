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

- [ ] **Boot autostart never fires.** `BootBroadcastReceiver` compares the action
      against the bare string `"BOOT_COMPLETED"`, but the system sends
      `"android.intent.action.BOOT_COMPLETED"`. Use `Intent.ACTION_BOOT_COMPLETED`
      (and `android.intent.action.QUICKBOOT_POWERON`).
- [ ] **"Started today" only compares day of month.** `ConfigProvider.wasStartedToday`
      compares `dayOfMonth` only, so a start date from an earlier month with the same
      day number counts as today and the masker skips that day. Compare `LocalDate`.
- [ ] **`settings.started.collect` re-enters itself in `MainActivity`.** Writing
      `setStartedDate()` inside the collector makes DataStore re-emit, and the second
      pass calls `finishAndRemoveTask()` right after `finish()`. Use `first()` for the
      one-shot decision. Covered by `MainActivityStartedCollectTest`.
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

_Nothing yet. Move items here with a short note and commit reference._
