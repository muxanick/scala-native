#include <filesystem>

namespace fs = std::filesystem;

extern "C" void* scalanative_cpp_filesystem_new_path(const char* path) {
    return new fs::path(path);
}

extern "C" void scalanative_cpp_filesystem_delete_path(void* path) {
    delete static_cast<fs::path*>(path);
}

extern "C" size_t scalanative_cpp_filesystem_path_string(void* path, char* buf) {
    if (!path)
        return 0;
    auto str = static_cast<fs::path*>(path)->u8string();
    std::memcpy(buf, str.c_str(), str.size() + 1);
    return str.size();
}

extern "C" void* scalanative_cpp_filesystem_path_filename(void* path) {
    if (!path)
        return nullptr;
    return new std::filesystem::path(static_cast<fs::path*>(path)->filename());
}

extern "C" void* scalanative_cpp_filesystem_path_parent_path(void* path) {
    if (!path)
        return nullptr;
    return new std::filesystem::path(static_cast<fs::path*>(path)->parent_path());
}