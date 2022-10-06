if(NOT TARGET cxx::cxx)
add_library(cxx::cxx STATIC IMPORTED)
set_target_properties(cxx::cxx PROPERTIES
    IMPORTED_LOCATION "C:/Users/Zacha/.gradle/caches/transforms-3/5434616ce51c8c06ce885af533242dce/transformed/cxx-1.2.0/prefab/modules/cxx/libs/android.x86/libcxx.a"
    INTERFACE_INCLUDE_DIRECTORIES "C:/Users/Zacha/.gradle/caches/transforms-3/5434616ce51c8c06ce885af533242dce/transformed/cxx-1.2.0/prefab/modules/cxx/include"
    INTERFACE_LINK_LIBRARIES ""
)
endif()

