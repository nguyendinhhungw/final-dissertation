import CrudTable from '@/components/admin/CrudTable';
const AdminServices = () => (
  <CrudTable table="services" title="Services" defaults={{ is_published: true, display_order: 0 }}
    fields={[
      { name: 'slug', label: 'Slug' },
      { name: 'title_vi', label: 'Title (VI)' },
      { name: 'title_en', label: 'Title (EN)' },
      { name: 'icon', label: 'Icon (smartphone, dollar-sign, palette, bar-chart)' },
      { name: 'short_vi', label: 'Short (VI)', type: 'textarea' },
      { name: 'short_en', label: 'Short (EN)', type: 'textarea' },
      { name: 'body_vi', label: 'Body (VI)', type: 'textarea' },
      { name: 'body_en', label: 'Body (EN)', type: 'textarea' },
      { name: 'display_order', label: 'Order', type: 'number' },
      { name: 'is_published', label: 'Published', type: 'switch' },
    ]} />
);
export default AdminServices;
