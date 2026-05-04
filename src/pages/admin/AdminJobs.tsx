import CrudTable from '@/components/admin/CrudTable';
const AdminJobs = () => (
  <CrudTable table="jobs" title="Jobs" defaults={{ is_open: true, display_order: 0 }}
    fields={[
      { name: 'slug', label: 'Slug' },
      { name: 'title_vi', label: 'Title (VI)' },
      { name: 'title_en', label: 'Title (EN)' },
      { name: 'department', label: 'Department' },
      { name: 'location', label: 'Location' },
      { name: 'employment_type', label: 'Type (Full-time...)' },
      { name: 'salary_range', label: 'Salary' },
      { name: 'short_vi', label: 'Short (VI)', type: 'textarea' },
      { name: 'short_en', label: 'Short (EN)', type: 'textarea' },
      { name: 'description_vi', label: 'Description (VI)', type: 'textarea' },
      { name: 'description_en', label: 'Description (EN)', type: 'textarea' },
      { name: 'requirements_vi', label: 'Requirements (VI)', type: 'textarea' },
      { name: 'requirements_en', label: 'Requirements (EN)', type: 'textarea' },
      { name: 'benefits_vi', label: 'Benefits (VI)', type: 'textarea' },
      { name: 'benefits_en', label: 'Benefits (EN)', type: 'textarea' },
      { name: 'display_order', label: 'Order', type: 'number' },
      { name: 'is_open', label: 'Open', type: 'switch' },
    ]} />
);
export default AdminJobs;
