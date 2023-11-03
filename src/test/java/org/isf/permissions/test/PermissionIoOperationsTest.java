package org.isf.permissions.test;
import org.isf.permissions.model.GroupPermission;
import org.isf.permissions.model.Permission;
import org.isf.permissions.service.GroupPermissionIoOperationRepository;
import org.isf.permissions.service.PermissionIoOperationRepository;
import org.isf.permissions.service.PermissionIoOperations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PermissionIoOperationsTest {
	@InjectMocks
	private PermissionIoOperations permissionIoOperations;

	@Mock
	private PermissionIoOperationRepository permissionRepository;

	@Mock
	private GroupPermissionIoOperationRepository groupPermissionRepository;

	@BeforeEach
	public void init() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testInsertPermission() {
		// Arrange
		Permission permission = new Permission();
		GroupPermission groupPermission1 = new GroupPermission();
		GroupPermission groupPermission2 = new GroupPermission();
		permission.setGroupPermission(List.of(groupPermission1, groupPermission2));

		when(permissionRepository.save(permission)).thenReturn(permission);
		when(groupPermissionRepository.save(any())).thenReturn(groupPermission1);

		// Act
		Permission result = permissionIoOperations.insertPermission(permission);

		// Assert
		assertNotNull(result);
		assertEquals(2, result.getGroupPermission().size());
	}

	@Test
	public void testDeletePermission() {
		// Arrange
		doNothing().when(permissionRepository).deleteById(anyInt());

		// Act
		Boolean result = permissionIoOperations.deletePermission(1);

		// Assertions
		assertTrue(result);
	}
}
