/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.template.soy;

import com.google.common.collect.ImmutableList;
import com.google.template.soy.base.SourceLocation;
import com.google.template.soy.error.ErrorReporter;
import com.google.template.soy.error.SoyError;
import com.google.template.soy.error.SoyErrorKind;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple {@link com.google.template.soy.error.ErrorReporter} implementation.
 *
 * @author brndn@google.com (Brendan Linn)
 */
public final class ErrorReporterImpl implements ErrorReporter {
  private final List<SoyError> errors = new ArrayList<>();
  private final SoyError.Factory errorFactory;

  // TODO(lukes): eliminate this default constructor.  It is currently used by a few expr nodes
  public ErrorReporterImpl() {
    this.errorFactory = SoyError.DEFAULT_FACTORY;
  }

  public ErrorReporterImpl(SoyError.Factory defaultFactory) {
    this.errorFactory = defaultFactory;
  }

  @Override
  public void report(SourceLocation sourceLocation, SoyErrorKind error, Object... args) {
    errors.add(errorFactory.create(sourceLocation, error, args));
  }

  @Override
  public Checkpoint checkpoint() {
    return new CheckpointImpl(errors.size());
  }

  @Override
  public boolean errorsSince(Checkpoint checkpoint) {
    // Throws a ClassCastException if callers try to pass in a Checkpoint instance
    // that wasn't returned by checkpoint(). We could probably ensure this at compile time
    // with a bunch of generics, but it's not worth the effort.
    return errors.size() > ((CheckpointImpl) checkpoint).numErrors;
  }

  /** Returns the full list of errors reported to this error reporter. */
  public Iterable<SoyError> getErrors() {
    return ImmutableList.copyOf(errors);
  }

  /**
   * Returns true if any errors have been reported.
   */
  boolean hasErrors() {
    return !errors.isEmpty();
  }

  private static final class CheckpointImpl implements Checkpoint {
    private final int numErrors;

    private CheckpointImpl(int numErrors) {
      this.numErrors = numErrors;
    }
  }
}
