#include <filesystem>

namespace fs = std::filesystem;

extern "C" bool scalanative_cpp_filesystem_is_file_str(const char* filename)
{
    return fs::is_regular_file(filename);
}

extern "C" bool scalanative_cpp_filesystem_is_directory_str(const char* filename)
{
    return fs::is_directory(filename);
}

extern "C" bool scalanative_filesystem_is_file(void* obj)
{
    if (!obj)
        return false;
    const auto& path = *static_cast<fs::path*>(obj);
    return fs::is_regular_file(path);
}

extern "C" bool scalanative_filesystem_is_directory(void* obj)
{
    if (!obj)
        return false;
    const auto& path = *static_cast<fs::path*>(obj);
    return fs::is_directory(path);
}

extern "C" void* scalanative_cpp_filesystem_directory_iterator(void* obj) {
    if (!obj)
        return nullptr;
    auto path = static_cast<fs::path*>(obj)->lexically_normal();
    if (!fs::is_directory(path))
        return new fs::directory_iterator(path.parent_path());
    else
        return new fs::directory_iterator(path);
}

extern "C" bool scalanative_cpp_filesystem_directory_iterator_increment(void* obj) {
    if (!obj)
        return false;
    auto it = static_cast<fs::directory_iterator*>(obj);
    it->operator++();
    return *it != fs::end(*it);
}

extern "C" const void* scalanative_cpp_filesystem_directory_iterator_value(void* obj) {
    if (!obj)
        return nullptr;
    auto it = static_cast<fs::directory_iterator*>(obj);
    auto entry = &(it->operator*());
    return entry;
}

extern "C" const uint64_t scalanative_cpp_filesystem_file_size(void* obj) {
    if (!obj)
        return 0;
    return std::filesystem::file_size(*static_cast<fs::path*>(obj));
}

extern "C" bool scalanative_cpp_filesystem_exists(void* obj) {
    if (!obj)
        return false;
    return std::filesystem::exists(*static_cast<fs::path*>(obj));
}