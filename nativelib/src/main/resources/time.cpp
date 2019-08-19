// cross-platform c++11 time
#include <chrono>

// return nanoseconds
long long steady_clock() {
    static const auto start = std::chrono::steady_clock::now();
    const auto end = std::chrono::steady_clock::now();
    const auto result =
        std::chrono::duration_cast<std::chrono::nanoseconds>(end - start);
    return result.count();
}

extern "C" long long scalanative_nano_time() { return steady_clock(); }

extern "C" long long scalanative_current_time_millis() {
    using namespace std::chrono;
    auto now_ms = time_point_cast<std::chrono::milliseconds>(system_clock::now());
    return now_ms.time_since_epoch().count();
}