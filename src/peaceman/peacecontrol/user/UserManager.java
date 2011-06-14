package peaceman.peacecontrol.user;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
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

	public UserManager(PeaceControl plugin) {
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
		return false;
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

	public boolean createUser(Player sender, String password, String email) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
    
}
