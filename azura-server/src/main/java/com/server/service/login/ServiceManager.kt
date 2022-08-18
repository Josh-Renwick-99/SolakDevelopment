package com.server.service.login

/**
 * Manages all [Service] implementations used by the game.
 *
 * @author Stan van der Bend
 */
object ServiceManager {

    /**
     * [Service] for handling login requests.
     */
    val loginService = LoginService()

    /**
     * Executes at the startup of the game, before any of the game assets are loaded.
     * Invokes [Service.init] for all [services][Service] contained within scope.
     */
    fun init(){
        loginService.init()
    }
}