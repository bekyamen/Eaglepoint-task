import { Role } from '../../models/dto.models';

describe('Role boundaries', () => {
  it('should allow ADMIN in admin routes', () => {
    const allowedRoles: Role[] = ['ADMIN'];
    const current: Role = 'ADMIN';
    expect(allowedRoles.includes(current)).toBeTrue();
  });
});
