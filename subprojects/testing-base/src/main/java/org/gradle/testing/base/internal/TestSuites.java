/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.testing.base.internal;

import org.gradle.api.Task;
import org.gradle.model.ModelMap;
import org.gradle.platform.base.test.TestSuiteBinarySpec;
import org.gradle.testing.base.TestSuiteTaskCollection;

public class TestSuites {
    public static void attachBinariesToCheckLifecycle(Task checkTask, ModelMap<? extends TestSuiteBinarySpec> binaries) {
        for (TestSuiteBinarySpec testBinary : binaries) {
            checkTask.dependsOn(((TestSuiteTaskCollection)testBinary.getTasks()).getRun());
        }
    }
}