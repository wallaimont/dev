
"""add closure workflow metadata

Revision ID: 0003_ticket_closure_workflow
Revises: 0002_ticket_attachments
Create Date: 2026-04-02
"""

from alembic import op
import sqlalchemy as sa

revision = '0003_ticket_closure_workflow'
down_revision = '0002_ticket_attachments'
branch_labels = None
depends_on = None


def upgrade() -> None:
    op.add_column('tickets', sa.Column('closure_requested_by_id', sa.Integer(), sa.ForeignKey('users.id'), nullable=True))
    op.add_column('tickets', sa.Column('closure_approved_by_id', sa.Integer(), sa.ForeignKey('users.id'), nullable=True))
    op.add_column('tickets', sa.Column('closure_requested_at', sa.DateTime(timezone=True), nullable=True))
    op.add_column('tickets', sa.Column('closure_approved_at', sa.DateTime(timezone=True), nullable=True))


def downgrade() -> None:
    op.drop_column('tickets', 'closure_approved_at')
    op.drop_column('tickets', 'closure_requested_at')
    op.drop_column('tickets', 'closure_approved_by_id')
    op.drop_column('tickets', 'closure_requested_by_id')
