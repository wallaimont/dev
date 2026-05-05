"""add ticket attachments

Revision ID: 0002_ticket_attachments
Revises: 0001_initial_schema
Create Date: 2026-04-02
"""

from alembic import op
import sqlalchemy as sa

revision = '0002_ticket_attachments'
down_revision = '0001_initial_schema'
branch_labels = None
depends_on = None


def upgrade() -> None:
    op.create_table(
        'ticket_attachments',
        sa.Column('id', sa.Integer(), primary_key=True),
        sa.Column('ticket_id', sa.Integer(), sa.ForeignKey('tickets.id', ondelete='CASCADE'), nullable=False),
        sa.Column('original_name', sa.String(length=255), nullable=False),
        sa.Column('stored_name', sa.String(length=255), nullable=False, unique=True),
        sa.Column('file_path', sa.String(length=500), nullable=False),
        sa.Column('content_type', sa.String(length=120), nullable=True),
        sa.Column('size_bytes', sa.Integer(), nullable=False, server_default='0'),
        sa.Column('uploaded_by_id', sa.Integer(), sa.ForeignKey('users.id'), nullable=False),
        sa.Column('created_at', sa.DateTime(timezone=True), server_default=sa.func.now(), nullable=False),
    )
    op.create_index('ix_ticket_attachments_id', 'ticket_attachments', ['id'])
    op.create_index('ix_ticket_attachments_ticket_id', 'ticket_attachments', ['ticket_id'])


def downgrade() -> None:
    op.drop_index('ix_ticket_attachments_ticket_id', table_name='ticket_attachments')
    op.drop_index('ix_ticket_attachments_id', table_name='ticket_attachments')
    op.drop_table('ticket_attachments')
