package com.ruse.net.login;

import com.ruse.GameSettings;
import com.ruse.net.PlayerSession;
import com.ruse.net.login.captcha.Captcha;
import com.ruse.net.login.captcha.CaptchaManager;
import com.ruse.net.packet.PacketBuilder;
import com.ruse.net.security.IsaacRandom;
import com.ruse.util.Misc;
import com.ruse.util.NameUtils;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;
import com.server.service.login.LoginServiceRequest;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.logging.Logger;

public final class LoginDecoderNew extends FrameDecoder {

    private static final int HANDSHAKE_STATE = 0;
    private static final int LOGGING_IN_STATE = 1;

    private static final int LOGIN_REQUEST_OPCODE = 14;
    private static final int MAGIC_ID = 0xFF;
    private static final int HIGH_MEMORY_STATUS = 0;
    private static final int LOW_MEMORY_STATUS = 1;

    private static final Logger LOGGER =  Logger.getLogger(LoginDecoderNew.class.getName());

    private int state = HANDSHAKE_STATE;
    private long encryptionSeed;

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        if (!channel.isConnected())
            return null;

        switch (state) {
            case HANDSHAKE_STATE:

                if (buffer.readableBytes() < 2)
                    return null;

                final int requestOpcode = buffer.readUnsignedByte();

                if (requestOpcode != LOGIN_REQUEST_OPCODE) {
                    LOGGER.warning("Received invalid handshake opcode {"+requestOpcode+"}");
                    channel.close();
                    return null;
                }
                // user hash
                buffer.skipBytes(1);

                encryptionSeed = new SecureRandom().nextLong();

                final PacketBuilder handshakeResponseBuilder = new PacketBuilder();
                handshakeResponseBuilder.putLong(0);
                handshakeResponseBuilder.put(0);
                handshakeResponseBuilder.putLong(encryptionSeed);

                channel.write(handshakeResponseBuilder.toPacket());
                state = LOGGING_IN_STATE;
                return null;
            case LOGGING_IN_STATE:

                if (buffer.readableBytes() < 2)
                    return null;

                final int loginRequestOpcode = buffer.readByte();

                if (loginRequestOpcode != 16 && loginRequestOpcode != 18) {
                    LOGGER.warning("Received invalid login request opcode {"+loginRequestOpcode+"}");
                    channel.close();
                    return null;
                }
                final int loginBlockLength = buffer.readByte() & 0xff;

                if (buffer.readableBytes() < loginBlockLength) {
                    LOGGER.warning("Received invalid login block length {"+loginBlockLength+"}");
                    channel.close();
                    return null;
                }
                final int magicId = buffer.readUnsignedByte();
                if (magicId != MAGIC_ID) {
                    LOGGER.warning("Received invalid magic id {"+magicId+"}");
                    channel.close();
                    return null;
                }
                final int clientVersion = buffer.readShort();
                final int memoryStatus = buffer.readByte();

                if (memoryStatus != HIGH_MEMORY_STATUS && memoryStatus != LOW_MEMORY_STATUS) {
                    LOGGER.warning("Received invalid memory status {"+memoryStatus+"}");
                    channel.close();
                    return null;
                }

                String hash = null;

                if (buffer.readableBytes() > 32) {

                    int length = buffer.readByte();

                    if (length < 32) {
                        channel.close();
                        return null;
                    }

                    byte[] bytes = new byte[length];

                    for (int i = 0; i < bytes.length; i++) {
                        bytes[i] = buffer.readByte();
                    }

                    hash = new String(bytes, StandardCharsets.UTF_8);

                }

                int pinCode = buffer.readShort();
                String authCode = Misc.readString(buffer);


                int length = buffer.readUnsignedByte();
                /*
                 * Our RSA components.
                 */
                try {
                    final int securityBlockLength = buffer.readUnsignedByte();
                    ChannelBuffer securityBuffer = buffer.readBytes(securityBlockLength);

                    BigInteger bigInteger = new BigInteger(securityBuffer.array());
                    bigInteger = bigInteger.modPow(GameSettings.RSA_EXPONENT, GameSettings.RSA_MODULUS);
                    securityBuffer = ChannelBuffers.wrappedBuffer(bigInteger.toByteArray());

                    final int securityId = securityBuffer.readByte();
                    if (securityId != 10) {
                        LOGGER.warning("Received invalid security id {"+securityId+"}");
                        channel.close();
                        return null;
                    }
                    final long clientSeed = securityBuffer.readLong();
                    final long seedReceived = securityBuffer.readLong();
                    if (seedReceived != encryptionSeed) {
                        LOGGER.warning("Received invalid encryption seed {"+seedReceived+"}");
                        channel.close();
                        return null;
                    }
                    final int[] seed = new int[4];
                    seed[0] = (int) (clientSeed >> 32);
                    seed[1] = (int) clientSeed;
                    seed[2] = (int) (this.encryptionSeed >> 32);
                    seed[3] = (int) this.encryptionSeed;

                    final IsaacRandom decodeIsaac = new IsaacRandom(seed);
                    for (int i = 0; i < seed.length; i++)
                        seed[i] += 50;
                    final int uid = securityBuffer.readInt();
                    String username = Misc.readString(securityBuffer);
                    final String password = Misc.readString(securityBuffer);
                    final String serial = Misc.readString(securityBuffer);
                    final String mac = Misc.readString(securityBuffer);

                    if (username.length() > 12 || password.length() > 20) {
                        LOGGER.warning("Username {"+username+"} or password {"+password+"} length too long");
                        return null;
                    }
                    username = Misc.formatText(username.toLowerCase());
                    final LoginDetailsMessage loginDetailsMessage = new LoginDetailsMessage(
                            channel,
                            username, password,
                            ((InetSocketAddress) channel.getRemoteAddress()).getAddress().getHostAddress(),
                            serial, mac, clientVersion, uid, hash, pinCode, authCode,
                            new IsaacRandom(seed),
                            decodeIsaac);

                    World.LOGIN_SERVICE.addLoginRequest(new LoginServiceRequest(loginDetailsMessage));
                    return null;
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    sendReturnCode(channel, LoginResponses.LOGIN_GAME_UPDATE, null);
                    return null;
                }
        }
        return null;
    }

    @SuppressWarnings("unused")
    public Player login(Channel channel, LoginDetailsMessage msg) {
        PlayerSession session = new PlayerSession(channel);

        Player player = new Player(session).setUsername(msg.getUsername())
                .setLongUsername(NameUtils.stringToLong(msg.getUsername())).setPassword(msg.getPassword())
                .setHostAddress(msg.getHost()).setSerialNumber(msg.getSerialNumber()).setMac(msg.getMac());

        session.setPlayer(player);

        System.out.println("Attempting to login " + player.getUsername() + " using hash " + msg.getHash()
                + " and serial " + msg.getSerialNumber() + ".");

        boolean found = false;

        for (String hash : GameSettings.CLIENT_HASH) {
            if (hash.equals(msg.getHash())) {
                found = true;
                break;
            }
        }

//			if (!found && !player.getUsername().equalsIgnoreCase("Test") && GameSettings.GAME_PORT != 43594) {
//				sendReturnCode(channel, LoginResponses.OLD_CLIENT_VERSION, player);
//				return null;
//			}

        int response = LoginResponses.getResponse(player, msg);
        System.out.println("response: " + response);

        final boolean newAccount = response == LoginResponses.NEW_ACCOUNT;

        if (newAccount) {
            player.setNewPlayer(true);
            response = LoginResponses.LOGIN_SUCCESSFUL;
        }

        Iterator<Player> $it = World.getLogoutQueue().iterator();

        for (; $it.hasNext();) {

            Player p = $it.next();

            if (p != null && p.getUsername().equalsIgnoreCase(player.getUsername())) {
                response = LoginResponses.LOGIN_ACCOUNT_ONLINE;
                break;
            }

        }
        System.out.println("1response: " + response);

        if (response != LoginResponses.LOGIN_ACCOUNT_ONLINE) {

            $it = World.getLoginQueue().iterator();

            for (; $it.hasNext();) {

                Player p = $it.next();

                if (p != null && p.getUsername().equalsIgnoreCase(player.getUsername())) {
                    response = LoginResponses.LOGIN_ACCOUNT_ONLINE;
                    break;
                }

            }

        }

        if (response != LoginResponses.LOGIN_ACCOUNT_ONLINE) {
            if (World.getLogoutQueue().contains(player) || World.getLoginQueue().contains(player)) {
                response = LoginResponses.LOGIN_ACCOUNT_ONLINE;
            }
        }
        System.out.println("2response: " + response);

        if (response == LoginResponses.LOGIN_SUCCESSFUL) {

            if (newAccount) {

                Captcha captcha = CaptchaManager.get(player.getUsername());

                if (captcha == null) {
                    captcha = CaptchaManager.create(player.getUsername());
                }

                byte[] image = new byte[0];

                int captchaResponse = 0;

                String text = msg.getAuthCode();

                if (text.length() == 4) {

                    CaptchaManager.getCollection().remove(player.getUsername().toLowerCase());

                    if (text.equalsIgnoreCase(captcha.getText())) {
                        captchaResponse = 0;
                    } else {
                        captchaResponse = 2;
                        captcha = CaptchaManager.create(player.getUsername());
                    }

                }

                if (captchaResponse != 0) {

                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(captcha.getImage(), "jpg", baos);
                        baos.flush();
                        image = baos.toByteArray();
                        baos.close();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }

                }

                PacketBuilder builder = new PacketBuilder().put((byte) 2).put((byte) player.getRights().ordinal())
                        .put((byte) 0).put((byte) 0);

                if (captchaResponse != 0) {
                    builder.putShort(image.length).putBytes(image, image.length);
                }

                channel.write(builder.toPacket());

                if (captchaResponse != 0) {
                    return null;
                }

            } else {
                channel.write(new PacketBuilder()
                        .put((byte) 2)
                        .put((byte) player.getRights().ordinal())
                        .put((byte) 0)
                        .put((byte) 0).toPacket());
            }

            if (!World.getLoginQueue().contains(player)) {
                World.getLoginQueue().add(player);
            }

            return player;

        } else {
            sendReturnCode(channel, response, player);
            return null;
        }
    }

    public static void sendReturnCode(final Channel channel, final int code, Player player) {
        PacketBuilder builder = new PacketBuilder().put((byte) code);
        if (code == LoginResponses.TWO_FACTOR_AUTH_START && player != null) {
            // builder.putString(player.getTwoFactorAuth().getPhoneNumber().toString());
        } else if (player != null) {
            System.out.println("Sending return code: " + code + " to " + player.getUsername());
        }
        System.out.println("return code: " + code);

        channel.write(builder.toPacket()).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture arg0) throws Exception {
                arg0.getChannel().close();
            }
        });
    }

}
