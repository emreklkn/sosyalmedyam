package com.example.sosyalmedyam.widget;

import androidx.annotation.NonNull;

/**
 *
 */
public interface Hashtagable {

  /** Unique id of this hashtag. */
  @NonNull
  CharSequence getId();

  /** Optional count, located right to hashtag name. */
  int getCount();
}
