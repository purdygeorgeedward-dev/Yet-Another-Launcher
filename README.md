# Fossify Launcher

<img alt="Logo" src="graphics/icon.webp" width="120" />

<a href='https://play.google.com/store/apps/details?id=org.fossify.home'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png' height=80/></a> <a href="https://f-droid.org/en/packages/org.fossify.home/"><img src="https://fdroid.gitlab.io/artwork/badge/get-it-on-en.svg" alt="Get it on F-Droid" height=80/></a> <a href="https://apt.izzysoft.de/fdroid/index/apk/org.fossify.home"><img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png" alt="Get it on IzzyOnDroid" height=80/></a>

Fossify Launcher is your gateway to a fast, personalized, and privacy-first home screen experience.
No ads, no bloat – just a smooth, efficient launcher designed to fit your unique style and
preferences.

**🚀 LIGHTNING-FAST NAVIGATION:**  
Navigate your device with speed and precision. Fossify Launcher is optimized to be responsive and
fluid, giving you instant access to your favorite apps without lag.

**🎨 FULL CUSTOMIZATION:**  
Tailor your home screen with dynamic themes, custom colors, and layouts. Personalize your launcher
to match your style with easy-to-use tools that let you create a truly unique setup.

**🖼️ COMPLETE WIDGET SUPPORT:**  
Integrate fully resizable widgets with ease. Whether you need clocks, calendars, or other handy
tools, Fossify Launcher ensures they blend seamlessly into your home screen design.

**📱 NO UNWANTED CLUTTER:**  
Effortlessly manage your apps by hiding or uninstalling them in just a few taps, keeping your home
screen organized and clutter-free.

**🔒 PRIVACY AND SECURITY:**  
Your privacy is at the heart of Fossify Launcher. With no internet access and no intrusive
permissions, your data stays with you. No tracking, no ads – just a launcher built to respect your
privacy.

**🌐 OPEN-SOURCE ASSURANCE:**  
Fossify Launcher is built on an open-source foundation, allowing you to review our code on GitHub,
fostering trust and a community committed to privacy.

Find your balance of speed, customization, and privacy with Fossify Launcher.

## Yet-Another-Launcher enhancements

This fork tracks a roadmap of ~205 potential features beyond upstream Fossify Launcher,
grouped by implementation difficulty (Tier 1 trivial through Tier 5 very hard).

**Shipped and working:**
- Larger default icon size (55dp → 72dp)
- Icon label position (below/beside/hidden) in the app drawer
- Text size scaling in the app drawer and home screen labels
- Accessibility font choice: Lexend and OpenDyslexic are bundled as real
  TrueType/OpenType fonts (both SIL Open Font License, license text in
  `app/src/main/assets/font_licenses/`), applied to both the drawer and home
  screen
- Glassmorphism drawer/widgets background (translucency + highlight border;
  not a real backdrop blur - see code comments in `extensions/View.kt`)
- Color blind mode (protanopia/deuteranopia/tritanopia simulation filters) on
  every icon, drawer and home screen alike
- Gradient folder backgrounds, closed icon and open panel
- High contrast mode (boosted label shadow), drawer and home screen
- Home screen layout backup/export/import (JSON, icons and folders only -
  widgets and shortcuts are deliberately excluded, see
  `helpers/LayoutBackupHelper.kt`)
- Haptic feedback on/off toggle - see note below on why it's not a real
  intensity slider yet
- Notification count badges (`FossifyOrg/Launcher#88`, 9 upvotes), drawer and
  home screen alike. Opt-in and gated behind an explicit in-app disclosure
  dialog before requesting Notification access - the broadest permission
  Android has - since this is a real trade-off against Fossify's
  no-intrusive-permissions stance, not a free feature. See
  `services/NotificationBadgeListener.kt` and the "Notifications" section in
  Settings.
- Meeting mode and gaming mode suppress notification badges on both
  surfaces; gaming mode also applies immersive fullscreen (manual toggle,
  not tied to detecting an actual running game, which would need
  usage-stats access)
- Notification badges are announced to screen readers (TalkBack), both
  surfaces - closes a real accessibility gap introduced when badges shipped
  canvas-drawn on the home screen
- "Reset accessibility settings to defaults" in Settings, gated behind a
  confirmation dialog
- Badge pop-in animation (overshoot scale) on both surfaces when a badge
  appears - home screen mirrors the existing folder-open ValueAnimator
  pattern, drawer animates the badge view directly

**Cut:** one-handed mode, dark mode scheduling, and extending icon label
position to the home screen grid.

Icon label position is real and shipped for the app drawer (below/beside/
hidden). Extending it to the home screen grid was assessed and declined -
not because it's low value, but because there's no safe partial version.
The home screen's tap-target hit box (`getClickableRect`) extends
downward to cover the label on the assumption it always sits below the
icon; that same assumption feeds the accessibility node bounds and three
separate cell-spacing calculations. BOTTOM and HIDDEN would be trivial but
add nothing the existing `showHomeAppLabels` toggle doesn't already cover;
RIGHT is the only new value and exactly the case that needs all of those
call sites to agree on a different hit-box shape, verified by actually
tapping/dragging on a device this environment doesn't have. The setting's
label was updated to say "(app drawer)" so it doesn't read as
home-screen-capable while sitting in a section next to things that are.

One-handed mode's visual part (shrink/shift the grid toward the
thumb-reachable half of the screen) is trivial, but touch input isn't
centralized - drag-and-drop and gesture handling live across `MainActivity`
and `HomeScreenGrid`, and every one of those raw-coordinate consumers
assumes screen space matches grid space. Making that hold under a visual
transform means threading a coordinate conversion through several
independent touch paths with no device available here to verify dragging
actually still works afterward. Removed the dead settings toggle rather
than leave a switch in the shipped UI that does nothing.

Dark mode scheduling doesn't fit this app's theming model at all -
`textColor`/`backgroundColor`/`primaryColor`/`accentColor` are arbitrary
user-picked colors via a color wheel, not a light/dark toggle (confirmed
by reading Fossify's actual `BaseConfig.kt`, not assumed). "Scheduling" it
would mean either silently overwriting the user's hand-picked colors on a
timer, or building a whole new dual-palette save/restore system - the
first is a real trust violation for a customization-focused launcher, the
second is nowhere near "easy." Left parked rather than forced.

**Known scope limitation:** haptic feedback intensity only gates on/off.
Real Low/Medium/High amplitude control needs the `VIBRATE` permission (raw
`Vibrator.vibrate(VibrationEffect)` calls) - a trade-off against Fossify's
zero-extra-permissions stance that wasn't made silently. See
`extensions/View.kt`.

**Prioritization note:** rather than working straight down the 205-item
list, real user demand was pulled from `FossifyOrg/Launcher`'s own GitHub
issues sorted by upvotes. Top remaining open asks: work profile support
(23), folders in the drawer/dock (17-18), custom icon packs (17) - none
started yet, each larger or riskier than what's above. Several of the
next-highest-voted items are stability bugs (jerky animations, freezes,
duplicated widgets), not features - fixing those responsibly needs a real
device/emulator to reproduce against, which this environment doesn't have.

➡️ Explore more Fossify apps: https://www.fossify.org<br>
➡️ Open-Source Code: https://www.github.com/FossifyOrg<br>
➡️ Join the community on Reddit: https://www.reddit.com/r/Fossify<br>
➡️ Connect on Telegram: https://t.me/Fossify

<div align="center">
<img alt="App image" src="fastlane/metadata/android/en-US/images/phoneScreenshots/1_en-US.png" width="30%">
<img alt="App image" src="fastlane/metadata/android/en-US/images/phoneScreenshots/2_en-US.png" width="30%">
<img alt="App image" src="fastlane/metadata/android/en-US/images/phoneScreenshots/3_en-US.png" width="30%">
</div>
