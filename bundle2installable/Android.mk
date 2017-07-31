LOCAL_PATH:= $(call my-dir)

## bundletool script

include $(CLEAR_VARS)

LOCAL_MODULE := bundle2installable
LOCAL_MODULE_TAG := optional
LOCAL_MODULE_CLASS := EXECUTABLES
LOCAL_IS_HOST_MODULE := true

include $(BUILD_SYSTEM)/base_rules.mk

$(LOCAL_BUILT_MODULE): $(HOST_OUT_JAVA_LIBRARIES)/bundle2installable$(COMMON_JAVA_PACKAGE_SUFFIX)
$(LOCAL_BUILT_MODULE): $(LOCAL_PATH)/etc/bundle2installable | $(ACP)
	@echo "Copy: $(PRIVATE_MODULE) ($@)"
	$(copy-file-to-new-target)
	$(hide) chmod 755 $@

## proto library

include $(CLEAR_VARS)

LOCAL_MODULE := bundle2installable-protos
LOCAL_MODULE_TAG := optional

LOCAL_PROTOC_OPTIMIZE_TYPE := full
LOCAL_SRC_FILES := $(call all-proto-files-under, src)
LOCAL_STATIC_JAVA_LIBRARIES := host-libprotobuf-java-full

include $(BUILD_HOST_JAVA_LIBRARY)

## tool jar

include $(CLEAR_VARS)

LOCAL_MODULE := bundle2installable

LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_IS_HOST_MODULE := true
LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := bundle2installable-protos jsr305lib

LOCAL_JAR_MANIFEST := manifest.txt

include $(BUILD_HOST_JAVA_LIBRARY)


