#!/system/bin/sh
# NEMESIS preuninstall (root; services already stopped). Also runs on the OLD
# package during `nemesis install --update`, after the new package has been
# extracted to $STAGE.
. /data/nemesis/apps/paytondev.launchpad/scripts/common.sh

log "preuninstall: removing daemon dalvik cache"
rm -f "$DEXCACHE"

if [ -f "$STAGE/NEMESIS.data" ]; then
    # Update: leave /system alone. The new postinstall rewrites the APK in place,
    # so Android keeps the app's data (favorites, settings).
    log "preuninstall: update in progress; leaving $SYSAPK for the new postinstall"
    exit 0
fi

[ -f "$SYSAPK" ] || { log "preuninstall: $SYSAPK not present"; exit 0; }
stop_app
if ! system_rw; then
    log "preuninstall: FAILED: nemesis remount rw failed; $SYSAPK left in place"
    exit 1
fi
rm -f "$SYSAPK"
sync
system_restore
if [ -f "$SYSAPK" ]; then
    log "preuninstall: FAILED: could not remove $SYSAPK"
    exit 1
fi
log "preuninstall: removed $SYSAPK"
exit 0
