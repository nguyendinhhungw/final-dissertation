import { useEffect, useState } from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { supabase } from '@/integrations/supabase/client';
import { Download, ExternalLink, Loader2 } from 'lucide-react';
import { useTranslation } from 'react-i18next';

interface Props {
  path: string | null;
  onOpenChange: (open: boolean) => void;
}

/**
 * Loads a private CV file from storage as a Blob and displays it in an
 * embedded iframe. Using a blob: URL avoids ad-blocker rules that block
 * URLs containing "supabase" (ERR_BLOCKED_BY_CLIENT).
 */
const CvViewerDialog = ({ path, onOpenChange }: Props) => {
  const { t, i18n } = useTranslation();
  const [blobUrl, setBlobUrl] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fileName = path ? path.split('/').pop() || 'cv.pdf' : 'cv.pdf';

  useEffect(() => {
    if (!path) return;
    let revoked: string | null = null;
    let cancelled = false;

    (async () => {
      setLoading(true);
      setError(null);
      const { data, error } = await supabase.storage.from('cv-uploads').download(path);
      if (cancelled) return;
      if (error || !data) {
        setError(error?.message || 'Failed to load CV');
        setLoading(false);
        return;
      }
      // Force PDF mime so the browser uses its built-in viewer
      const blob = data.type ? data : new Blob([data], { type: 'application/pdf' });
      const url = URL.createObjectURL(blob);
      revoked = url;
      setBlobUrl(url);
      setLoading(false);
    })();

    return () => {
      cancelled = true;
      if (revoked) URL.revokeObjectURL(revoked);
      setBlobUrl(null);
    };
  }, [path]);

  const downloadFile = () => {
    if (!blobUrl) return;
    const a = document.createElement('a');
    a.href = blobUrl;
    a.download = fileName;
    document.body.appendChild(a);
    a.click();
    a.remove();
  };

  const openInNewTab = () => {
    if (!blobUrl) return;
    window.open(blobUrl, '_blank', 'noopener,noreferrer');
  };

  return (
    <Dialog open={!!path} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-5xl w-[95vw] h-[90vh] p-0 flex flex-col gap-0">
        <DialogHeader className="px-5 py-3 border-b border-border flex flex-row items-center justify-between space-y-0">
          <DialogTitle className="truncate">{fileName}</DialogTitle>
          <div className="flex items-center gap-2 mr-8">
            <Button size="sm" variant="outline" onClick={openInNewTab} disabled={!blobUrl}>
              <ExternalLink className="h-4 w-4 mr-1.5" />
              {i18n.language === 'vi' ? 'Mở tab mới' : 'Open in new tab'}
            </Button>
            <Button size="sm" variant="outline" onClick={downloadFile} disabled={!blobUrl}>
              <Download className="h-4 w-4 mr-1.5" />
              {i18n.language === 'vi' ? 'Tải về' : 'Download'}
            </Button>
          </div>
        </DialogHeader>
        <div className="flex-1 bg-muted overflow-hidden">
          {loading && (
            <div className="h-full grid place-items-center text-muted-foreground">
              <Loader2 className="h-6 w-6 animate-spin" />
            </div>
          )}
          {error && (
            <div className="h-full grid place-items-center text-destructive p-8 text-center">
              {error}
            </div>
          )}
          {blobUrl && !loading && !error && (
            <iframe
              src={blobUrl}
              title={fileName}
              className="w-full h-full border-0"
            />
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default CvViewerDialog;
