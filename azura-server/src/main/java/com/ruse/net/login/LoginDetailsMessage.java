package com.ruse.net.login;

import com.ruse.net.packet.Packet;
import com.ruse.net.security.IsaacRandom;
import org.jboss.netty.channel.Channel;

/**
 * The {@link Packet} implementation that contains data used for the final
 * portion of the login protocol.
 *
 * @author lare96 <http://github.org/lare96>
 */
public final class LoginDetailsMessage {

    /**
     * The username of the player.
     */
    private final Channel context;
    private final String username;

    /**
     * The password of the player.
     */
    private final String password;

    /**
     * The player's host address
     */
    private final String host;

    /**
     * The player's serial number.
     */
    private final String serial_number;

    /**
     * The player's client version.
     */
    private final int clientVersion;

    /**
     * The player's client uid.
     */
    private final int uid;
    private final String mac;

    /**
     *
     */
    private final String hash;

    /**
     *
     */
    private final int pinCode;

    /**
     *
     */
    private final String authCode;


    private final IsaacRandom encryptor;
    private final IsaacRandom decryptor;

    public LoginDetailsMessage(Channel channel,
                               String username,
                               String password,
                               String host,
                               String serial_number,
                               String mac,
                               int clientVersion,
                               int uid,
                               String hash,
                               int pinCode,
                               String authCode,
                               IsaacRandom encryptor,
                               IsaacRandom decryptor) {
        this.context = channel;
        this.username = username;
        this.password = password;
        this.host = host;
        this.serial_number = serial_number;
        this.clientVersion = clientVersion;
        this.mac = mac;
        this.uid = uid;
        this.hash = hash;
        this.pinCode = pinCode;
        this.authCode = authCode;
        this.encryptor = encryptor;
        this.decryptor = decryptor;
    }


    public Channel getChannel() {
        return context;
    }
    /**
     * Gets the username of the player.
     *
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password of the player.
     *
     * @return the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the password of the player.
     *
     * @return the password.
     */
    public String getHost() {
        return host;
    }

    /**
     * Gets the player's serial number.
     *
     * @return the serial number.
     */
    public String getSerialNumber() {
        return serial_number;
    }

    /**
     * Gets the player's client version.
     *
     * @return the client version.
     */
    public int getClientVersion() {
        return clientVersion;
    }

    /**
     * Gets the player's client uid.
     *
     * @return the client's uid.
     */
    public int getUid() {
        return uid;
    }

    public String getHash() {
        return hash;
    }

    public int getPinCode() {
        return pinCode;
    }

    public String getAuthCode() {
        return authCode;
    }
    public String getMac() {
        return mac;
    }
    public IsaacRandom getEncryptor() {
        return encryptor;
    }

    public IsaacRandom getDecryptor() {
        return decryptor;
    }

}