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

**Settings UI exists, not yet wired into rendering:**
Dark mode scheduling, one-handed mode, gaming/meeting mode, icon label
position on the home screen grid (drawer only so far - the home screen grid
draws labels via `Canvas.drawText` and shares its coordinate math with
drag/drop hit-rects, so repositioning there needs more care).

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
