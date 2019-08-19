#include <filesystem>

namespace fs = std::filesystem;

extern "C" const void* scalanative_cpp_filesystem_DirectoryEntry_path(void* obj) {
    if (!obj)
        return nullptr;
    const auto& path = static_cast<fs::directory_entry*>(obj)->path();
    return &path;
}
