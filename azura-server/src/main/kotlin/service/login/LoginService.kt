package com.server.service.login

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.ruse.net.PlayerSession
import com.ruse.net.packet.codec.PacketDecoder
import com.ruse.net.packet.codec.PacketEncoder
import com.ruse.net.security.IsaacRandom
import com.ruse.world.World
import com.server.service.Service
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.logging.Logger

/**
 * TODO: add documentation
 *
 * @author  Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @since   24/11/2019
 * @version 1.0
 */
class LoginService: Service {

    private val logger: Logger = Logger.getLogger(LoginService::javaClass.name)
    val requests = LinkedBlockingQueue<LoginServiceRequest>()

    private var threadCount = 1

    override fun init() {
        val executorService = Executors
                .newFixedThreadPool(threadCount, ThreadFactoryBuilder()
                        .setNameFormat("login-worker")
                        .setUncaughtExceptionHandler { t, e -> logger.severe("Error with thread $t, ${e.message}") }.build())
        for (i in 0 until threadCount)
            executorService.execute(LoginWorker(this))
    }

    override fun postLoad() {
    }

    override fun bindNet() {}

    override fun terminate() {}

    fun addLoginRequest(msg: LoginServiceRequest) {
        requests.offer(msg)
    }

    fun successfulLogin(session: PlayerSession, encoderIsaac: IsaacRandom, decoderIsaac: IsaacRandom){

        val channel = session.channel!!
        val pipeline = channel.pipeline

        channel.attachment = session.player

        if(channel.isOpen){

            pipeline.replace("encoder", "encoder", PacketEncoder(encoderIsaac))
            pipeline.replace("decoder", "decoder", PacketDecoder(decoderIsaac))

            World.getLoginQueue().offer(session.player)
        }
    }
}