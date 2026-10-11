package com.paytondev.launchpad;

final class ScreenFlow {
    enum Page { HOME, ADVANCED, BROWSER }
    static Page back(Page page) {
        if (page == Page.BROWSER) return Page.ADVANCED;
        if (page == Page.ADVANCED) return Page.HOME;
        return null;
    }
}
