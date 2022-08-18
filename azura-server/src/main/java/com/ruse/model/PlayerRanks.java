package com.ruse.model;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class PlayerRanks {

	/*
	 * A member who has donated to the server.
	 */
	public enum DonorRights {
		NONE(1), DONOR(1), DELUXE_DONOR(1.25), SPONSOR(1.5);

		private static final ImmutableSet<DonorRights> MEMBERS = Sets.immutableEnumSet(DONOR, DELUXE_DONOR, SPONSOR);

		/**
		 * The experience modifier
		 */
		private double modifier;

		/**
		 * New donator right
		 * 
		 * @param modifier the experience modifier
		 */
		DonorRights(double modifier) {
			this.setModifier(modifier);
		}

		/**
		 * Gets the modifier
		 *
		 * @return the modifier
		 */
		public double getModifier() {
			return modifier;
		}

		/**
		 * Sets the modifier
		 *
		 * @param modifier the modifier
		 */
		public void setModifier(double modifier) {
			this.modifier = modifier;
		}

		public boolean isMember() {
			return MEMBERS.contains(this);
		}
	}

	/**
	 * Represents a player's privilege rights.
	 */
	public enum PlayerRights {
		PLAYER(-1, null, 1, 1), OWNER(-1, "<shad=1>@cya@", 1, 1), DEVELOPER(-1, "<shad=B40404>", 1, 1),
		WEB_DEVELOPER(-1, null, 1, 1), GLOBAL_ADMINISTRATOR(-1, null, 1, 1),
		ADMINISTRATOR(-1, "<col=FFFF64><shad=0>", 1, 1), FORUM_ADMINISTRATOR(-1, null, 1, 1),
		GLOBAL_MODERATOR(-1, null, 1, 1), TRIAL_MODERATOR(-1, null, 1, 1), MODERATOR(-1, "<col=20B2AA><shad=0>", 1, 1),
		FORUM_MODERATOR(-1, null, 1, 1), TRIAL_FORUM_MODERATOR(-1, null, 1, 1), ITEM_MODELLER(-1, null, 1, 1),
		GFX_ARTIST(-1, null, 1, 1), YOUTUBER(-1, null, 1, 1), EX_STAFF(-1, null, 1, 1);

		private static final ImmutableSet<PlayerRights> STAFF = Sets.immutableEnumSet(DEVELOPER,
				GLOBAL_ADMINISTRATOR, ADMINISTRATOR, GLOBAL_MODERATOR, MODERATOR);

		/**
		 * Gets the rank for a certain id.
		 * 
		 * @param id The id (ordinal()) of the rank.
		 * @return rights.
		 */
		public static PlayerRights forId(int id) {
			for (PlayerRights rights : PlayerRights.values()) {
				if (rights.ordinal() == id) {
					return rights;
				}
			}
			return null;
		}

		private int yellDelay;
		private String yellHexColorPrefix;
		private double loyaltyPointsGainModifier;
		private double experienceGainModifier;

		PlayerRights(int yellDelaySeconds, String yellHexColorPrefix, double loyaltyPointsGainModifier,
				double experienceGainModifier) {
			this.yellDelay = yellDelaySeconds;
			this.yellHexColorPrefix = yellHexColorPrefix;
			this.loyaltyPointsGainModifier = loyaltyPointsGainModifier;
			this.experienceGainModifier = experienceGainModifier;
		}

		public double getExperienceGainModifier() {
			return experienceGainModifier;
		}

		public double getLoyaltyPointsGainModifier() {
			return loyaltyPointsGainModifier;
		}

		public int getYellDelay() {
			return yellDelay;
		}

		public String getYellPrefix() {
			return yellHexColorPrefix;
		}

		public boolean isStaff() {
			return STAFF.contains(this);
		}

		public boolean isDeveloper() {
			return this == PlayerRights.DEVELOPER;
		}

		public static PlayerRights forName(String s) {
			for (PlayerRights rights : PlayerRights.values()) {
				if (rights.name().equalsIgnoreCase(s)) {
					return rights;
				}
			}
			return PlayerRights.PLAYER;
		}
	}

}
