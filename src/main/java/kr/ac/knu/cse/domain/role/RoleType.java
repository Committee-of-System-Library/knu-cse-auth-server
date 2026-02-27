package kr.ac.knu.cse.domain.role;

/*
### RoleType

- `ROLE_USER`
- `ROLE_EXEC`
- `ROLE_FINANCE`
- `ROLE_ADMIN`
- `ROLE_MEMBER`

> ROLE_MEMBER is not a persisted role.
>
> It is a **derived role** calculated in the AuthorizationPolicy
> based on the Dues status.
>
> The calculated result is included only in the token.
*/


public enum RoleType {
    ROLE_USER,
    ROLE_EXEC,
    ROLE_FINANCE,
    ROLE_ADMIN,
    ROLE_MEMBER
}
