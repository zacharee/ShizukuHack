if(NOT TARGET boringssl::crypto_static)
add_library(boringssl::crypto_static STATIC IMPORTED)
set_target_properties(boringssl::crypto_static PROPERTIES
    IMPORTED_LOCATION "C:/Users/Zacha/.gradle/caches/transforms-3/d6b532aeaa847fb3bc32d3750d6ee4d3/transformed/boringssl-2.1/prefab/modules/crypto_static/libs/android.arm64-v8a/libcrypto_static.a"
    INTERFACE_INCLUDE_DIRECTORIES "C:/Users/Zacha/.gradle/caches/transforms-3/d6b532aeaa847fb3bc32d3750d6ee4d3/transformed/boringssl-2.1/prefab/modules/crypto_static/include"
    INTERFACE_LINK_LIBRARIES ""
)
endif()

if(NOT TARGET boringssl::ssl_static)
add_library(boringssl::ssl_static STATIC IMPORTED)
set_target_properties(boringssl::ssl_static PROPERTIES
    IMPORTED_LOCATION "C:/Users/Zacha/.gradle/caches/transforms-3/d6b532aeaa847fb3bc32d3750d6ee4d3/transformed/boringssl-2.1/prefab/modules/ssl_static/libs/android.arm64-v8a/libssl_static.a"
    INTERFACE_INCLUDE_DIRECTORIES "C:/Users/Zacha/.gradle/caches/transforms-3/d6b532aeaa847fb3bc32d3750d6ee4d3/transformed/boringssl-2.1/prefab/modules/ssl_static/include"
    INTERFACE_LINK_LIBRARIES "boringssl::crypto_static"
)
endif()

