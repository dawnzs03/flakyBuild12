/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.io.gcp.bigtable;

import static org.apache.beam.vendor.guava.v32_1_2_jre.com.google.common.base.Preconditions.checkArgument;

import com.google.auto.value.AutoValue;
import com.google.cloud.bigtable.config.BigtableOptions;
import java.io.Serializable;
import org.apache.beam.sdk.annotations.Internal;
import org.apache.beam.sdk.extensions.gcp.auth.CredentialFactory;
import org.apache.beam.sdk.extensions.gcp.options.GcpOptions;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.apache.beam.vendor.guava.v32_1_2_jre.com.google.common.annotations.VisibleForTesting;
import org.apache.beam.vendor.guava.v32_1_2_jre.com.google.common.base.MoreObjects;
import org.checkerframework.checker.nullness.qual.Nullable;

/** Configuration for a Cloud Bigtable client. */
@AutoValue
@SuppressWarnings({
  "nullness" // TODO(https://github.com/apache/beam/issues/20497)
})
@Internal
public abstract class BigtableConfig implements Serializable {

  /** Returns the project id being written to. */
  public abstract @Nullable ValueProvider<String> getProjectId();

  /** Returns the instance id being written to. */
  public abstract @Nullable ValueProvider<String> getInstanceId();

  /** Returns the app profile being read from. */
  public abstract @Nullable ValueProvider<String> getAppProfileId();

  /**
   * Returns the Google Cloud Bigtable instance being written to, and other parameters.
   *
   * @deprecated will be replaced by bigtable options configurator.
   */
  @Deprecated
  abstract @Nullable BigtableOptions getBigtableOptions();

  /** Configurator of the effective Bigtable Options. */
  abstract @Nullable SerializableFunction<BigtableOptions.Builder, BigtableOptions.Builder>
      getBigtableOptionsConfigurator();

  /** Weather validate that table exists before writing. */
  abstract boolean getValidate();

  /** Bigtable emulator. */
  abstract @Nullable String getEmulatorHost();

  /** User agent for this job. */
  abstract @Nullable String getUserAgent();

  /**
   * Credentials for running the job. Use the default credentials in {@link GcpOptions} if it's not
   * set.
   */
  abstract @Nullable CredentialFactory getCredentialFactory();

  /** Get number of channels. */
  abstract @Nullable Integer getChannelCount();

  abstract Builder toBuilder();

  static BigtableConfig.Builder builder() {
    return new AutoValue_BigtableConfig.Builder();
  }

  @AutoValue.Builder
  abstract static class Builder {

    abstract Builder setProjectId(ValueProvider<String> projectId);

    abstract Builder setInstanceId(ValueProvider<String> instanceId);

    abstract Builder setAppProfileId(ValueProvider<String> appProfileId);

    /** @deprecated please set the options directly in BigtableIO. */
    @Deprecated
    abstract Builder setBigtableOptions(BigtableOptions options);

    abstract Builder setValidate(boolean validate);

    /** @deprecated please set the options directly in BigtableIO. */
    @Deprecated
    abstract Builder setBigtableOptionsConfigurator(
        SerializableFunction<BigtableOptions.Builder, BigtableOptions.Builder> optionsConfigurator);

    abstract Builder setEmulatorHost(String emulatorHost);

    abstract Builder setUserAgent(String userAgent);

    abstract Builder setCredentialFactory(CredentialFactory credentialFactory);

    abstract Builder setChannelCount(int count);

    abstract BigtableConfig build();
  }

  public BigtableConfig withProjectId(ValueProvider<String> projectId) {
    checkArgument(projectId != null, "Project Id of BigTable can not be null");
    return toBuilder().setProjectId(projectId).build();
  }

  public BigtableConfig withInstanceId(ValueProvider<String> instanceId) {
    checkArgument(instanceId != null, "Instance Id of BigTable can not be null");
    return toBuilder().setInstanceId(instanceId).build();
  }

  BigtableConfig withAppProfileId(ValueProvider<String> appProfileId) {
    checkArgument(appProfileId != null, "App profile id can not be null");
    return toBuilder().setAppProfileId(appProfileId).build();
  }

  /** @deprecated please set the options directly in BigtableIO. */
  @Deprecated
  public BigtableConfig withBigtableOptions(BigtableOptions options) {
    checkArgument(options != null, "Bigtable options can not be null");
    return toBuilder().setBigtableOptions(options).build();
  }

  /** @deprecated please set the options directly in BigtableIO. */
  @Deprecated
  public BigtableConfig withBigtableOptionsConfigurator(
      SerializableFunction<BigtableOptions.Builder, BigtableOptions.Builder> configurator) {
    checkArgument(configurator != null, "configurator can not be null");
    return toBuilder().setBigtableOptionsConfigurator(configurator).build();
  }

  public BigtableConfig withValidate(boolean isEnabled) {
    return toBuilder().setValidate(isEnabled).build();
  }

  @VisibleForTesting
  public BigtableConfig withEmulator(String emulatorHost) {
    checkArgument(emulatorHost != null, "emulatorHost can not be null");
    return toBuilder().setEmulatorHost(emulatorHost).build();
  }

  void validate() {
    checkArgument(
        (getProjectId() != null
                && (!getProjectId().isAccessible() || !getProjectId().get().isEmpty()))
            || (getBigtableOptions() != null
                && getBigtableOptions().getProjectId() != null
                && !getBigtableOptions().getProjectId().isEmpty()),
        "Could not obtain Bigtable project id");

    checkArgument(
        (getInstanceId() != null
                && (!getInstanceId().isAccessible() || !getInstanceId().get().isEmpty()))
            || (getBigtableOptions() != null
                && getBigtableOptions().getInstanceId() != null
                && !getBigtableOptions().getInstanceId().isEmpty()),
        "Could not obtain Bigtable instance id");
  }

  void populateDisplayData(DisplayData.Builder builder) {
    builder
        .addIfNotNull(
            DisplayData.item("projectId", getProjectId()).withLabel("Bigtable Project Id"))
        .addIfNotNull(
            DisplayData.item("instanceId", getInstanceId()).withLabel("Bigtable Instance Id"))
        .addIfNotNull(
            DisplayData.item("appProfileId", getAppProfileId())
                .withLabel("Bigtable App Profile Id"));

    if (getBigtableOptions() != null) {
      builder.add(
          DisplayData.item("bigtableOptions", getBigtableOptions().toString())
              .withLabel("Bigtable Options"));
    }
  }

  boolean isDataAccessible() {
    return (getProjectId() == null || getProjectId().isAccessible())
        && (getInstanceId() == null || getInstanceId().isAccessible())
        && (getAppProfileId() == null || getAppProfileId().isAccessible());
  }

  @Override
  public final String toString() {
    return MoreObjects.toStringHelper(BigtableConfig.class)
        .add("projectId", getProjectId())
        .add("instanceId", getInstanceId())
        .add("appProfileId", getAppProfileId())
        .add("userAgent", getUserAgent())
        .add("emulator", getEmulatorHost())
        .toString();
  }
}
