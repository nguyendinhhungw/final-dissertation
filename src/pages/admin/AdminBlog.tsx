import CrudTable from '@/components/admin/CrudTable';
const AdminBlog = () => (
  <CrudTable
    table="blog_posts"
    title="Blog Posts"
    orderBy="display_order"
    defaults={{ is_published: true, is_featured: false, display_order: 0, author: 'Merryblue', tags: [] }}
    fields={[
      { name: 'slug', label: 'Slug' },
      { name: 'title_vi', label: 'Title (VI)' },
      { name: 'title_en', label: 'Title (EN)' },
      { name: 'category', label: 'Category' },
      { name: 'excerpt_vi', label: 'Excerpt (VI)', type: 'textarea' },
      { name: 'excerpt_en', label: 'Excerpt (EN)', type: 'textarea' },
      { name: 'body_vi', label: 'Body (VI - Markdown)', type: 'textarea' },
      { name: 'body_en', label: 'Body (EN - Markdown)', type: 'textarea' },
      { name: 'cover_url', label: 'Cover Image URL' },
      { name: 'tags', label: 'Tags (comma)', type: 'array' },
      { name: 'author', label: 'Author' },
      { name: 'display_order', label: 'Order', type: 'number' },
      { name: 'is_featured', label: 'Featured', type: 'switch' },
      { name: 'is_published', label: 'Published', type: 'switch' },
    ]}
  />
);
export default AdminBlog;
