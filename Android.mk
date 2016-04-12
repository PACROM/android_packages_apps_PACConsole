#
# Copyright (C) 2016 PAC-ROM
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_PACKAGE_NAME := PACConsole
LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true
LOCAL_PROGUARD_ENABLED := disabled

LOCAL_MODULE_TAGS := optional

LOCAL_AAPT_INCLUDE_ALL_RESOURCES := true

LOCAL_STATIC_JAVA_LIBRARIES += android-support-design \
    android-support-v4 \
    android-support-v7-recyclerview \
    android-support-v7-appcompat

LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res
LOCAL_RESOURCE_DIR += $(TOP)/frameworks/support/v7/appcompat/res
LOCAL_RESOURCE_DIR += $(TOP)/frameworks/support/design/res
LOCAL_RESOURCE_DIR += $(TOP)/frameworks/support/v7/recyclerview/res

LOCAL_SRC_FILES := $(call all-subdir-java-files, java)

LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages android.support.v7.appcompat \
    --extra-packages android.support.design \
    --extra-packages android.support.v7.recyclerview


include $(BUILD_PACKAGE)
