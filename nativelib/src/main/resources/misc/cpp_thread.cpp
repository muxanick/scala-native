#include <thread>

namespace {
uint16_t getThreadId(const std::thread *t) {
    auto id = t->get_id();
    return *(reinterpret_cast<uint16_t *>(&(id)));
}

template <typename T> T castToFunc(void *f) { return reinterpret_cast<T>(f); }
}

typedef void (*scalanative_thread_func)(int);

extern "C" void *scalanative_cpplib_threadStart(void *threadMemory,
                                                scalanative_thread_func functor,
                                                int threadId) {

    // join previous thread if exists
    auto t = reinterpret_cast<std::thread *>(threadMemory);
    if (t) {
        t->join();
        // call descturtor
        t->~thread();
        // call constructor of new thread by reusing same memory
        t = new (threadMemory) std::thread(functor, threadId);
    } else // fist time, need to allocate
    {
        t = new std::thread(functor, threadId);
    }

    return t;
}

extern "C" void scalanative_cpplib_threadJoin(void *threadMemory) {
    auto t = reinterpret_cast<std::thread *>(threadMemory);
    if (t) {
        t->join();
        // call descturtor
        t->~thread();
    }
}

extern "C" void scalanative_cpp_threadFreeMemory(void *threadMemory) {
    auto t = reinterpret_cast<std::thread *>(threadMemory);
    delete t;
}