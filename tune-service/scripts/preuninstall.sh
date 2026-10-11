#!/system/bin/sh
# NEMESIS preuninstall (root; services already stopped). Only removes what this
# package added: the daemon's dalvik-cache entry. The dex itself lives under the
# package tree and is deleted with it.
. /data/nemesis/apps/paytondev.launchpad/scripts/common.sh
log "preuninstall: removing daemon dalvik cache"
rm -f "$DEXCACHE"
exit 0
