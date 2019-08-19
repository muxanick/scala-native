#include <fstream>

extern "C" bool scalanative_cpp_ios_fstream_is_open(void* obj)
{
    if (!obj)
        return false;
    return static_cast<std::fstream *>(obj)->is_open();
}

extern "C" void* scalanative_cpp_ios_fstream_open(const char* filename, int bitmask)
{
    auto f = new std::fstream(filename, bitmask);
    return f;
}