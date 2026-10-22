#!/system/bin/sh
# NEMESIS type=execute: first-time install of the Launchpad package (TUNE service
# + /system/app/Launchpad.apk).
. "${0%/*}/lib.sh"

[ -f "$PKGZIP" ] || { say "FAIL: missing $PKGZIP"; finish 1; }
if pkg_installed; then
    say "FAIL: $PKG_ID is already installed. Use the update bundle instead."
    finish 1
fi
run "$NEMESIS" install "$PKGZIP" || finish 1
verify_installed
finish $?
