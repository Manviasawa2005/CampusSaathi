# CampusSaathi — Database Design

## 1. Database Overview

CampusSaathi uses a MySQL relational database named `campussaathi_db` to manage user accounts, assignments, student submissions, announcements, class schedules, college information, and complaints.

## 2. Database Tables

| Table | Purpose |
|---|---|
| `users` | Stores user details, email, password hash, role, status, and batch. |
| `assignments` | Stores assignment details, subject, batch, marks, creator, and optional PDF filename. |
| `assignment_submissions` | Tracks student submission status and marks obtained. |
| `announcements` | Stores announcements and their intended audience. |
| `class_schedule` | Stores subjects, batches, class timings, classrooms, and teachers. |
| `college_info` | Stores college information by category. |
| `complaints` | Stores complaints, submitter details, and complaint status. |

## 3. Database Relationships

- `assignments.created_by` references `users.user_id`.
- `assignment_submissions.assignment_id` references `assignments.assignment_id`.
- `assignment_submissions.student_id` references `users.user_id`.
- `assignment_submissions.marked_by` references `users.user_id`.
- `announcements.sender_id` and `announcements.target_user_id` reference `users.user_id`.
- `class_schedule.teacher_id` references `users.user_id`.
- `complaints.submitted_by` references `users.user_id`.

## 4. Data Integrity

- User email addresses are unique.
- Assignment names are unique for a given subject and batch.
- Each student has at most one submission record per assignment, enforced by a unique constraint.
- Foreign keys maintain relationships between related tables.

## 5. Database Schema Export

The database structure was exported from MySQL Workbench using the **Dump Structure Only** option.

Schema file: [`CampusSaathi_Schema.sql`](../../CampusSaathi_Schema.sql)

## 6. Entity–Relationship Diagram

The following diagram represents the database structure and relationships.

![CampusSaathi Entity–Relationship Diagram](../../CampusSaathi_ER_Diagram.png)
