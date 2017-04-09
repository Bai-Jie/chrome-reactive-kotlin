package pl.wendigo.chrome
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.ReplaySubject
import okhttp3.OkHttpClient
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

internal class ChromeDebuggerConnection constructor(
        private val frames: FramesStream,
        private val mapper: FrameMapper
) : DebuggerProtocol {
    private val eventNameToClassMapping: ConcurrentHashMap<String, Class<out ProtocolEvent>> = ConcurrentHashMap()
    private val nextRequestId = AtomicLong(0)

    /**
     * Registers event name to class mappings.
     */
    override fun registerEventMappings(mapOf: Map<String, Class<out ProtocolEvent>>) {
        eventNameToClassMapping.putAll(mapOf)
    }

    /**
     * Closes connection to remote debugger.
     */
    override fun close() {
       frames.close()
    }

    /**
     * Sends request and captures response.
     */
    override fun <T> runAndCaptureResponse(name: String, params: Any?, clazz: Class<T>) : Single<T> {
        return Single.fromCallable {
            RequestFrame(
                id = nextRequestId.incrementAndGet(),
                method = name,
                params = params
            )
        }.flatMap { request ->
            frames.send(request).flatMap { result ->
                if (result == true) {
                    frames.getResponse(request, clazz)
                } else {
                    Single.error(RequestFailed(request, "Could not enqueue message"))
                }
            }
        }
    }

    /**
     * Captures events by given name and casts received messages to specified class.
     */
    override fun <T> captureEvents(name : String, outClazz: Class<T>) : Flowable<T> where T : ProtocolEvent {
        return frames.eventFrames()
            .filter { frame -> frame.method == name }
            .flatMapSingle { frame ->  mapper.deserializeEvent(frame, outClazz) }
            .subscribeOn(Schedulers.io())
            .toFlowable(BackpressureStrategy.BUFFER)
    }

    /**
     * Captures all events as generated by remote debugger
     */
    override fun captureAllEvents() : Flowable<ProtocolEvent> {
        return frames.eventFrames()
            .flatMapSingle { frame -> mapper.deserializeEvent(frame, eventNameToClassMapping[frame.method] ?: ProtocolEvent::class.java) }
            .subscribeOn(Schedulers.io())
            .toFlowable(BackpressureStrategy.LATEST)
    }

    companion object {
        /**
         * Opens new ChromeDebuggerConnection session for given websocket uri.
         */
        @JvmStatic
        fun openSession(url: String, eventBufferSize: Int = 128) : ChromeDebuggerConnection {
            val mapper = FrameMapper()
            val frames = WebsocketFramesStream(url, ReplaySubject.create(eventBufferSize), mapper, OkHttpClient())

            return ChromeDebuggerConnection(
                    frames,
                    mapper
            )
        }
    }
}