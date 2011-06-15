package peaceman.peacecontrol.user;

import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import peaceman.peacecontrol.PeaceControl;

/**
 *
 * @author peaceman
 */
public class UserManager {
	private final PeaceControl plugin;
	private final HashMap<String, User> usersByUsername;
	private final HashMap<Long, User> usersByUserid;

	public UserManager(PeaceControl plugin) {
		this.usersByUsername = new HashMap<String, User>();
		this.usersByUserid = new HashMap<Long, User>();
		this.plugin = plugin;
	}
	
	public boolean createUser(String username, String password, String email) throws Exception {
		if (this.usernameExists(username))
			return false;
		if (email != null && this.emailExists(email)) {
			return false;
		}
		Session session = this.plugin.sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		
		User tmpUser = new User();
		tmpUser.setUsername(username);
		tmpUser.setEmail(email);
		tmpUser.setPasshash(this.genPasshash(password));
		tmpUser.setRegisteredAt(new Date());
		
		session.save(tmpUser);
		tx.commit();
		session.close();
		this.addUserToCache(tmpUser);
		return true;
	}
	
	private void addUserToCache(User user) {
		if (user.getId() == 0) {
			throw new RuntimeException("Cant add a user to cache if he doesnt has an id");
		}
		this.usersByUserid.put(user.getId(), user);
		this.usersByUsername.put(user.getUsername(), user);
	}
	
	private void removeUserFromCache(User user) {
		if (user.getId() == 0) {
			throw new RuntimeException("Cant remove a user from cache if he doesnt has an id");
		}
		this.usersByUserid.remove(user.getId());
		this.usersByUsername.remove(user.getUsername());
	}
	
	public String genPasshash(String password) throws Exception {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(password.getBytes("UTF-8"));
		byte[] digest = md5.digest();
		
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < digest.length; i++) {
			int unsigned = digest[i] & 0xff;
			sb.append(Integer.toHexString(unsigned));
		}
		
		return sb.toString();
	}

	public void loginUser(Player sender) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public boolean usernameExists(String username) {
		Session session = this.plugin.sessionFactory.openSession();
		session.beginTransaction();
		Criteria crit = session.createCriteria(User.class);
		crit.add(Restrictions.eq("username", username));
		User tmpUser = (User) crit.uniqueResult();		
		session.getTransaction().commit();
		session.close();
		if (tmpUser == null) {
			return false;
		} else {
			return true;
		}	
		
	}

	public boolean emailExists(String email) {
		Session session = this.plugin.sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria crit = session.createCriteria(User.class);
		crit.add(Restrictions.eq("email", email));
		User tmpUser = (User)crit.uniqueResult();
		tx.commit();
		session.close();
		
		if (tmpUser == null) {
			return false;
		} else {
			return true;
		}
	}

	public boolean createUser(Player sender, String password, String email) throws Exception {
		return this.createUser(sender.getDisplayName(), password, email);
	}

	public User getUser(String username) {
		Session session = this.plugin.sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria crit = session.createCriteria(User.class);
		crit.add(Restrictions.eq("username", username));
		User returnUser = (User)crit.uniqueResult();
		return returnUser;
	}
	
	public User getUser(Long id) {
		if (this.usersByUserid.containsKey(id))
			return this.usersByUserid.get(id);
		
		return this.loadUserFromDatabase(id);
	}
	
	private User loadUserFromDatabase(Long id) {
		Session session = this.plugin.sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria crit = session.createCriteria(User.class);
		crit.add(Restrictions.eq("id", id));
		User user = (User)crit.uniqueResult();
		
		if (user == null) {
			throw new RuntimeException("Couldn't find a user with id " + id + " in Database");
		}
		
		this.addUserToCache(user);
		return user;
	}
	
	private User loadUserFromDatabase(String username) {
		Session session = this.plugin.sessionFactory.openSession();
		Transaction tx = session.beginTransaction();
		Criteria crit = session.createCriteria(User.class);
		crit.add(Restrictions.eq("username", username));
		User user = (User)crit.uniqueResult();
		
		if (user == null) {
			throw new RuntimeException("Couldn't find a user with username " + username + " in Database");
		}
		
		this.addUserToCache(user);
		return user;
	}
	
	public User getUser(Player player) {
		return this.getUser(player.getDisplayName());
	}
    
}
