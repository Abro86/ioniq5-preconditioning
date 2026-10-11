# Shared by the hooks and the service. Toolbox only.
export PATH=/sbin:/vendor/bin:/system/sbin:/system/bin:/system/xbin
export LD_LIBRARY_PATH=/vendor/lib:/system/lib
HERE=/data/nemesis/apps/paytondev.launchpad
# The APK is used only as the dex container (CLASSPATH) for the knob daemon under
# app_process. It is NOT installed into PackageManager, so no signature/install
# policy applies to it.
DEX=$HERE/app/Launchpad.apk
# dalvik-cache entry app_process may create for that dex (named after its path).
DEXCACHE=/data/dalvik-cache/data@nemesis@apps@paytondev.launchpad@app@Launchpad.apk@classes.dex

log() { echo "$(date '+%Y-%m-%d %H:%M:%S') launchpad $*"; }
