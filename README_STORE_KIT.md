# Floofies: Cute Merge Puzzle — Build + Play Store Kit

## 1. Build (Android Studio)
1. Android Studio > Open > select the `Floofies` folder > wait for Gradle sync.
2. Run on phone/emulator (test ads show automatically).
3. Release: Build > Generate Signed Bundle (AAB). Upload AAB to Play Console.
4. Launcher icon: right-click `app` > New > Image Asset (512x512 Floof face), then add
   `android:icon="@mipmap/ic_launcher"` in AndroidManifest `<application>`.
5. If `gradlew` missing: File > Settings > Gradle > use Gradle 8.7, or run `gradle wrapper`.

## 2. Go live with real ads (IMPORTANT)
Replace the TEST ids (Google test ids will NOT earn money; real ids on test devices = ban risk):
- AndroidManifest.xml  -> APPLICATION_ID (ca-app-pub-xxx~yyy)
- MainActivity.java    -> BANNER_ID, INTERSTITIAL_ID, REWARDED_ID

## 3. Ad logic in this build
- Banner: bottom, adaptive.
- Interstitial: on "Play Again" every 2nd game.
- Rewarded: "Continue (Watch Ad)" on game over, once per game, clears the top of the pile.

## 4. Fixes applied (vs original)
1. Tap drop-position fixed (touchstart sets aim, touchend no double-drop)
2. Canvas 2:3 aspect ratio, no squash on small screens
3. Reset re-randomizes Floofs, clears drop timer
4. 2048 = win screen + Keep Playing; 2048+2048 = Mega Pop +4096
5. AudioContext created on first tap
6. 3 collision iterations = stable stacking, merge-on-touch
7. Game-over check ignores fast-moving bouncing balls (fewer false overflows)

## 5. Privacy Policy (host on GitHub Pages / Google Sites and paste URL in Play Console)
**Privacy Policy — Floofies: Cute Merge Puzzle**
Floofies does not collect personal information directly. The game stores your best score locally on your device only.
We use Google AdMob to show ads. AdMob may collect and use device identifiers (such as advertising ID), IP address and
usage data to serve and measure ads. See https://policies.google.com/privacy and https://support.google.com/admob/answer/6128543.
The app has no accounts, no location access and no user-generated content. Children under 13 are not knowingly targeted;
ads shown are from Google's family-safe policies where applicable.
Contact: YOUR_EMAIL@example.com

## 6. Play Store Listing
**Title (30):** Floofies: Cute Merge Puzzle
**Short (80):** Drop, merge and pop adorable Floofies! Can you reach the big 2048 Floof?
**Full:**
Meet the Floofies, the fluffiest merge puzzle ever!
Drop cute pom-pom Floofies into the box. When two matching Floofies touch, they merge into a bigger, cuter one.
Keep merging all the way to the legendary 2048 Floof, without overflowing the line!
FEATURES
- One-tap, easy to learn, hard to master
- Adorable pastel Floofies with 11 evolution levels
- Satisfying pop sounds and sparkle effects
- Chain merges for huge scores
- Offline play, no internet needed
- Beat your best score and challenge friends
Tap, merge, relax. Download Floofies now!
**Category:** Games > Puzzle | **Tags:** merge, puzzle, cute, casual, offline
**Data safety:** Collects Device or other IDs (Advertising ID) for Advertising; no data shared except via AdMob; no account.
**Content rating:** Everyone. **Ads:** Yes.
