package com.ruse.net;

import com.ruse.net.packet.Packet;
import com.ruse.world.World;
import com.ruse.world.entity.impl.player.Player;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelUpstreamHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of netty's {@link SimpleChannelUpstreamHandler} to handle
 * all of netty's incoming events.
 * 
 * @author Gabriel Hannason
 */
public class ChannelHandler extends IdleStateAwareChannelUpstreamHandler {

	/**
	 * The logger for this class.
	 */
	private static final Logger logger = Logger.getLogger(ChannelHandler.class.getName());

	public ChannelHandler() {
	}
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		if (!(e.getCause() instanceof IOException)) { // Because there's no more data to read (happens when client has
														// been forcibly exited via task manager)
			logger.log(Level.WARNING, "Exception occurred for channel: " + e.getChannel() + ", closing...", e.getCause());
			ctx.getChannel().close();
		}
	}

	@Override
	public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {
		e.getChannel().close();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		final Object attachment = ctx.getChannel().getAttachment();
		final Object message = e.getMessage();

		if (attachment instanceof Player) {
			if (message instanceof Packet) {
				final Player player = (Player) attachment;
				final Packet packet = (Packet) message;
				player.getSession().handleIncomingMessage(packet);
			}
		}
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		final Object attachment = ctx.getChannel().getAttachment();

		if (attachment instanceof Player) {
			final Player player = (Player) attachment;
			if (player.getSession().getState() != SessionState.LOGGED_OUT) {
				if (!World.getLogoutQueue().contains(player)) {
					player.getLogoutTimer().reset();
					World.getLogoutQueue().add(player);
				}
			}
		}
	}

}
