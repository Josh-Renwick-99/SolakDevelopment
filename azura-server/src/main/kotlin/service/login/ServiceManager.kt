package com.server.service.login

/**
 * Manages all [LoginService] implementations used by the game.
 *
 * @author Stan van der Bend
 */
object ServiceManager {

    /**
     * [LoginService] for handling login requests.
     */
    val loginService = LoginService()

    /**
     * Executes at the startup of the game, before any of the game assets are loaded.
     * Invokes [LoginService.init] for all [LoginService] contained within scope.
     */
    fun init(){
        loginService.init()
    }
}