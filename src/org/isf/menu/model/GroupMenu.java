package org.isf.menu.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.isf.utils.db.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/*------------------------------------------
 * User - model for the user entity
 * -----------------------------------------
 * modification history
 * ? - ? - first version 
 * 07/05/2016 - Antonio - ported to JPA
 * 
 *------------------------------------------*/
@Entity
@Table(name="GROUPMENU")
@EntityListeners(AuditingEntityListener.class) 
@AttributeOverrides({
    @AttributeOverride(name="createdBy", column=@Column(name="GM_CREATED_BY")),
    @AttributeOverride(name="createdDate", column=@Column(name="GM_CREATED_DATE")),
    @AttributeOverride(name="lastModifiedBy", column=@Column(name="GM_LAST_MODIFIED_BY")),
    @AttributeOverride(name="active", column=@Column(name="GM_ACTIVE")),
    @AttributeOverride(name="lastModifiedDate", column=@Column(name="GM_LAST_MODIFIED_DATE"))
})
public class GroupMenu extends Auditable<String>
{
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="GM_ID")		
	private Integer code;
	
	@Column(name="GM_UG_ID_A")
	private String userGroup;
	
	@Column(name="GM_MNI_ID_A")
	private String menuItem;

	@Transient
	private volatile int hashCode = 0;
	
	public GroupMenu(){
	}
	
	public GroupMenu(String userGroup, String menuItem)
	{
		this.userGroup = userGroup;
		this.menuItem = menuItem;
	}
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getUserGroup() {
		return userGroup;
	}
	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}
	public String getMenuItem() {
		return menuItem;
	}
	public void setMenuItem(String menuItem) {
		this.menuItem = menuItem;
	}
	public int getActive() {
		return active;
	}
	public void setActive(char active) {
		this.active = active;
	}
	
	@Override
	public boolean equals(Object anObject) {
        return (anObject == null) || !(anObject instanceof GroupMenu) ? false
                : (getCode().equals(((GroupMenu) anObject).getCode())
                  && getUserGroup().equalsIgnoreCase(((GroupMenu) anObject).getUserGroup()) 
                  && getMenuItem().equals(((GroupMenu) anObject).getMenuItem())
                  && getActive() == ((GroupMenu) anObject).getActive());
    }

}//class GroupMenu
