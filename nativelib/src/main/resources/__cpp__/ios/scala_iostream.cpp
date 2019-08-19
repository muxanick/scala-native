#include <iostream>

extern "C" int scalanative_cpp_ios_openmode_app() { return std::ios::app; }
extern "C" int scalanative_cpp_ios_openmode_binary() { return std::ios::binary; }
extern "C" int scalanative_cpp_ios_openmode_in() { return std::ios::in; }
extern "C" int scalanative_cpp_ios_openmode_out() { return std::ios::out; }
extern "C" int scalanative_cpp_ios_openmode_trunc() { return std::ios::trunc; }
extern "C" int scalanative_cpp_ios_openmode_ate() { return std::ios::ate; }

extern "C" int scalanative_cpp_ios_seekdir_beg() { return std::ios::beg; }
extern "C" int scalanative_cpp_ios_seekdir_end() { return std::ios::end; }
extern "C" int scalanative_cpp_ios_seekdir_cur() { return std::ios::cur; }

extern "C" void* scalanative_cpp_ios_stdin() { return &std::cin; }
extern "C" void* scalanative_cpp_ios_stdout() { return &std::cout; }
extern "C" void* scalanative_cpp_ios_stderr() { return &std::cerr; }

extern "C" std::streamsize scalanative_cpp_ios_iostream_streambuf_in_avail(void* obj)
{
    if (!obj)
        return 0;
    auto stream = static_cast<std::istream *>(obj);
    std::streamsize pos = stream->tellg();
    stream->seekg(0, std::ios::end);
    std::streamsize end = stream->tellg();
    stream->seekg(pos, std::ios::beg);
    return end - pos;
}

extern "C" void scalanative_cpp_ios_iostream_write(void* obj, const char* buf, size_t count)
{
    if (!obj)
        return;
    static_cast<std::ostream *>(obj)->write(buf, count);
}

extern "C" void scalanative_cpp_ios_iostream_read(void* obj, char* buf, size_t count)
{
    if (!obj)
        return;
    static_cast<std::iostream *>(obj)->read(buf, count);
}

extern "C" void scalanative_cpp_ios_iostream_seekg(void* obj, std::streamsize offset, int dir)
{
    if (!obj)
        return;
    static_cast<std::iostream *>(obj)->seekg(offset, dir);
}

extern "C" std::streamsize scalanative_cpp_ios_iostream_tellg(void* obj) {
    if (!obj)
        return 0;
    return static_cast<std::iostream *>(obj)->tellg();
}