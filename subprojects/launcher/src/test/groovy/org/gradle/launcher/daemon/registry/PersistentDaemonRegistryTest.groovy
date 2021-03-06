/*
 * Copyright 2012 the original author or authors.
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

package org.gradle.launcher.daemon.registry

import org.gradle.internal.nativeintegration.ProcessEnvironment
import org.gradle.internal.nativeintegration.filesystem.Chmod
import org.gradle.launcher.daemon.context.DaemonContext
import org.gradle.launcher.daemon.context.DaemonContextBuilder
import org.gradle.internal.remote.Address
import org.gradle.test.fixtures.file.TestNameTestDirectoryProvider
import org.junit.Rule
import spock.lang.Specification

import static org.gradle.cache.internal.DefaultFileLockManagerTestHelper.createDefaultFileLockManager
import static org.gradle.cache.internal.DefaultFileLockManagerTestHelper.unlockUncleanly

class PersistentDaemonRegistryTest extends Specification {

    @Rule TestNameTestDirectoryProvider tmp = new TestNameTestDirectoryProvider()

    int addressCounter = 0
    def lockManager = createDefaultFileLockManager()
    def file = tmp.file("registry")
    def registry = new PersistentDaemonRegistry(file, lockManager, Stub(Chmod))

    def "corrupt registry file is ignored"() {
        given:
        registry.store(new DaemonInfo(address(), daemonContext(), "password".bytes, true))

        expect:
        registry.all.size() == 1

        when:
        unlockUncleanly(file)

        then:
        registry.all.empty
    }

    def "safely removes from registry file"() {
        given:
        def address = address()

        and:
        registry.store(new DaemonInfo(address, daemonContext(), "password".bytes, true))

        when:
        registry.remove(address)

        then:
        registry.all.empty

        and: //it is safe to remove it again
        registry.remove(address)
    }

    def "safely removes if registry empty"() {
        given:
        def address = address()

        when:
        registry.remove(address)

        then:
        registry.all.empty
    }

    def "mark busy ignores entry that has been removed"() {
        given:
        def address = address()

        when:
        registry.markBusy(address)

        then:
        registry.all.empty
    }

    def "mark idle ignores entry that has been removed"() {
        given:
        def address = address()

        when:
        registry.markIdle(address)

        then:
        registry.all.empty
    }

    def "safely removes stop events when empty"() {
        when:
        registry.removeStopEvents([])

        then:
        registry.stopEvents.empty
    }

    def "clears single stop event when non-empty"() {
        given:
        def stopEvent = new DaemonStopEvent(new Date(1L), "STOP_REASON")
        registry.storeStopEvent(stopEvent)

        when:
        registry.removeStopEvents([stopEvent])

        then:
        registry.stopEvents.empty
    }

    def "clears multiple stop events when non-empty"() {
        given:
        def stopEvents = [
            new DaemonStopEvent(new Date(1L), "STOP_REASON"),
            new DaemonStopEvent(new Date(42L), "ANOTHER_STOP_REASON")
        ]
        stopEvents.each { registry.storeStopEvent(it) }

        when:
        registry.removeStopEvents(stopEvents)

        then:
        registry.stopEvents.empty
    }

    DaemonContext daemonContext() {
        new DaemonContextBuilder([maybeGetPid: {null}] as ProcessEnvironment).with {
            daemonRegistryDir = tmp.createDir("daemons")
            create()
        }
    }

    Address address(int i = addressCounter++) {
        new TestAddress(i.toString())
    }

    private static class TestAddress implements Address {

        final String displayName

        TestAddress(String displayName) {
            this.displayName = displayName
        }

        boolean equals(o) {
            displayName == o.displayName
        }

        int hashCode() {
            displayName.hashCode()
        }
    }

}
