import CrudTable from '@/components/admin/CrudTable';
const AdminPortfolio = () => (
  <CrudTable table="portfolio_projects" title="Portfolio" defaults={{ is_published: true, display_order: 0, tech_stack: [] }}
    fields={[
      { name: 'slug', label: 'Slug' },
      { name: 'title_vi', label: 'Title (VI)' },
      { name: 'title_en', label: 'Title (EN)' },
      { name: 'category', label: 'Category' },
      { name: 'short_vi', label: 'Short (VI)', type: 'textarea' },
      { name: 'short_en', label: 'Short (EN)', type: 'textarea' },
      { name: 'body_vi', label: 'Body (VI)', type: 'textarea' },
      { name: 'body_en', label: 'Body (EN)', type: 'textarea' },
      { name: 'cover_url', label: 'Cover URL' },
      { name: 'tech_stack', label: 'Tech Stack (comma)', type: 'array' },
      { name: 'display_order', label: 'Order', type: 'number' },
      { name: 'is_published', label: 'Published', type: 'switch' },
    ]} />
);
export default AdminPortfolio;
