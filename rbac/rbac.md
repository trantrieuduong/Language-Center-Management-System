| Panel                       | ADMIN | STAFF_CONSULTANT | STAFF_ACCOUNTANT | TEACHER | STUDENT |
|-----------------------------|---|-----------------|-------------|---|---|
| Dashboard                   | ✓ | ✓               | ✓           | ✓ | ✓ |
| Students                    | ✓ | ✓               | ✓ (read)    | ✗ | self only |
| Teachers                    | ✓ | ✓               | ✗           | ✗ | ✗ |
| Courses                     | ✓ | ✓               | ✗           | ✓ (read) | ✗ |
| Classes (Read, add, update) | ✓ | ✓               | ✗           | own only | ✗ |
| Enrollments                 | ✓ | ✓         | ✓ (read)    | ✗ | self only |
| Rooms                       | ✓ | ✓               | ✗           | ✗ | ✗ |
| Schedules                   | ✓ | ✓               | ✗           | own only | self only |
| Attendance (read, update)   | ✓ | ✓ (read)        | ✗           | own class | self only |
| Results (read, update)      | ✓ | ✓ (read)        | ✗           | own class | self only |
| Invoices                    | ✓ | ✗               | ✓           | ✗ | self only |
| Payments                    | ✓ | ✗               | ✓           | ✗ | self only |
| Staff                       | ✓ | ✗               | ✗           | ✗ | ✗ |
| User Accounts               | ✓ | ✗               | ✗           | ✗ | ✗ |

Note:
- Use soft delete in delete operation in UserAccount, Student, Teacher, Staff, Invoice, Payment entity
- Only admin have deletion permission