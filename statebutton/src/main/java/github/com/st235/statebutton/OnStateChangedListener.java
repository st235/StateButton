// Copyright (с) 2017 by Alexander Dadukin (st235@yandex.ru)
// All rights reserved.

package github.com.st235.statebutton;

/**
 * State change listener
 */
public interface OnStateChangedListener {
    void onStateChanged(boolean isEnabled, int state);
}