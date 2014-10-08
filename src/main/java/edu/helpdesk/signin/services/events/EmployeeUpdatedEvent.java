package edu.helpdesk.signin.services.events;

import edu.helpdesk.signin.model.dto.Employee;

public class EmployeeUpdatedEvent implements ApplicationEvent{
	private Employee editor;
	private Employee originalEmployee;
	private Employee updateEmployee;
	
	public EmployeeUpdatedEvent(Employee original, Employee updated, Employee editor) {
		this.originalEmployee = original;
		this.updateEmployee = updated;
		this.editor = editor;
	}
	
	public EmployeeUpdatedEvent() {
	}
	
	public Employee getOriginalEmployee() {
		return originalEmployee;
	}
	public void setOriginalEmployee(Employee originalEmployee) {
		this.originalEmployee = originalEmployee;
	}
	public Employee getUpdateEmployee() {
		return updateEmployee;
	}
	public void setUpdateEmployee(Employee updateEmployee) {
		this.updateEmployee = updateEmployee;
	}
	public Employee getEditor() {
		return editor;
	}
	public void setEditor(Employee editor) {
		this.editor = editor;
	}
	@Override
	public String toString() {
		return "EmployeeUpdatedEvent [editor=" + editor + ", originalEmployee="
				+ originalEmployee + ", updateEmployee=" + updateEmployee + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((editor == null) ? 0 : editor.hashCode());
		result = prime
				* result
				+ ((originalEmployee == null) ? 0 : originalEmployee.hashCode());
		result = prime * result
				+ ((updateEmployee == null) ? 0 : updateEmployee.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmployeeUpdatedEvent other = (EmployeeUpdatedEvent) obj;
		if (editor == null) {
			if (other.editor != null)
				return false;
		} else if (!editor.equals(other.editor))
			return false;
		if (originalEmployee == null) {
			if (other.originalEmployee != null)
				return false;
		} else if (!originalEmployee.equals(other.originalEmployee))
			return false;
		if (updateEmployee == null) {
			if (other.updateEmployee != null)
				return false;
		} else if (!updateEmployee.equals(other.updateEmployee))
			return false;
		return true;
	}
}
