/* This code is part of Freenet. It is distributed under the GNU General
 * Public License, version 2 (or at your option any later version). See
 * http://www.gnu.org/ for further details of the GPL. */
package freenet.node;

import freenet.crypt.BlockCipher;

/**
 * Class representing a single session key.
 * @author Matthew Toseland <toad@amphibian.dyndns.org> (0xE43DA450)
 */
public class KeyTracker {
	
	/** A PacketTracker may have more than one KeyTracker, but a KeyTracker 
	 * may only have one PacketTracker. In other words, in some cases it is
	 * possible to change the session key without invalidating the packet
	 * sequence, but it is never possible to invalidate the packet sequence
	 * without changing the session key. */
	final PacketTracker packets;
	
	private static boolean logMINOR;
	/** Parent PeerNode */
	public final PeerNode pn;
	/** Cipher to both encrypt outgoing packets with and decrypt
	 * incoming ones. */
	public final BlockCipher sessionCipher;
	/** Key for above cipher, so far for debugging */
	public final byte[] sessionKey;

	KeyTracker(PeerNode parent, PacketTracker tracker, BlockCipher cipher, byte[] sessionKey) {
		this.pn = parent;
		this.packets = tracker;
		this.sessionCipher = cipher;
		this.sessionKey = sessionKey;
	}

}
