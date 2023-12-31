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
package org.apache.beam.sdk.options;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;

import org.apache.beam.vendor.guava.v32_1_2_jre.com.google.common.collect.ImmutableList;
import org.junit.Test;

public class PortablePipelineOptionsTest {

  /** Defaults should only be changed with a very good reason and need to match across SDKs. */
  @Test
  public void testDefaults() {
    PortablePipelineOptions options = PipelineOptionsFactory.as(PortablePipelineOptions.class);
    assertThat(options.getFilesToStage(), is(nullValue()));
    assertThat(options.getJobEndpoint(), is(nullValue()));
    assertThat(options.getDefaultEnvironmentType(), is(nullValue()));
    assertThat(options.getDefaultEnvironmentConfig(), is(nullValue()));
    assertThat(options.getSdkWorkerParallelism(), is(1));
    assertThat(options.getEnvironmentCacheMillis(), is(0));
    assertThat(options.getEnvironmentExpirationMillis(), is(0));
    assertThat(options.getOutputExecutablePath(), is(nullValue()));
    assertThat(options.getEnvironmentOptions(), is(nullValue()));
  }

  @Test
  public void getEnvironmentOption() {
    PortablePipelineOptions options = PipelineOptionsFactory.as(PortablePipelineOptions.class);
    options.setEnvironmentOptions(ImmutableList.of("foo=bar"));
    assertEquals("bar", PortablePipelineOptions.getEnvironmentOption(options, "foo"));
  }

  @Test
  public void getEnvironmentOptionContainingEquals() {
    PortablePipelineOptions options = PipelineOptionsFactory.as(PortablePipelineOptions.class);
    options.setEnvironmentOptions(ImmutableList.of("foo=bar=baz"));
    assertEquals("bar=baz", PortablePipelineOptions.getEnvironmentOption(options, "foo"));
  }

  @Test
  public void getEnvironmentOptionFromEmptyListReturnsEmptyString() {
    PortablePipelineOptions options = PipelineOptionsFactory.as(PortablePipelineOptions.class);
    assertEquals("", PortablePipelineOptions.getEnvironmentOption(options, "foo"));
  }

  @Test
  public void getEnvironmentOptionMissingOptionReturnsEmptyString() {
    PortablePipelineOptions options = PipelineOptionsFactory.as(PortablePipelineOptions.class);
    options.setEnvironmentOptions(ImmutableList.of("foo=bar"));
    assertEquals("", PortablePipelineOptions.getEnvironmentOption(options, "baz"));
  }
}
