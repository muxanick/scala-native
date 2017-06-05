#ifdef _WIN32
#include <Windows.h>
#include <stdio.h>

#include "os_win_mman.h"

extern "C" int mprotect(void *addr, size_t len, int prot) {
    static int oldProt;
    if (prot == PROT_NONE) {
#ifdef SCALA_NATIVE_EXPERIMENTAL_MEMORY_SAFEPOINT
        return VirtualProtect(addr, len, PAGE_NOACCESS | PAGE_GUARD,
                              &oldProt) == TRUE
                   ? 0
                   : -1;
#else
        return 0;
#endif
    } else {
#ifdef SCALA_NATIVE_EXPERIMENTAL_MEMORY_SAFEPOINT
        return VirtualProtect(addr, len, PAGE_READ, oldProt) == TRUE ? 0 : -1;
#else
        return 0;
#endif
    }
    return -1;
}

extern "C" void *mmap(void *addr, size_t length, int prot, int flags, int fd,
                      off_t offset) {
    HANDLE hMapFile;
    LPCTSTR pBuf;

    hMapFile = CreateFileMappingW(
        INVALID_HANDLE_VALUE,       // use paging file
        NULL,                       // default security
        PAGE_READWRITE,             // read/write access
        (length >> 32),             // maximum object size (high-order DWORD)
        (length & 0xFFFFFFFF),      // maximum object size (low-order DWORD)
        L"scalanative_memory_map"); // name of mapping object

    if (hMapFile == NULL) {
        printf("Could not create file mapping object (%lu).\n", GetLastError());
        return 0;
    }
    pBuf = (LPTSTR)MapViewOfFile(hMapFile,            // handle to map object
                                 FILE_MAP_ALL_ACCESS, // read/write permission
                                 0, 0, length);

    if (pBuf == NULL) {
        printf("Could not map view of file (%lu).\n", GetLastError());
        CloseHandle(hMapFile);

        return 0;
    }
    return (void *)pBuf;
}
extern "C" int munmap(void *addr, size_t length) { return 0; }

#endif