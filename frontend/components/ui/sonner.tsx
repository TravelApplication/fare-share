'use client';

import { useTheme } from 'next-themes';
import { Toaster as Sonner } from 'sonner';

type ToasterProps = React.ComponentProps<typeof Sonner>;

const Toaster = ({ ...props }: ToasterProps) => {
  const { theme = 'system' } = useTheme();

  return (
    <Sonner
      theme={theme as ToasterProps['theme']}
      className="toaster group"
      toastOptions={{
        classNames: {
          toast:
            'group toast group-[.toaster]:bg-blue-500  group-[.toaster]:border-none group-[.toaster]:text-white group-[.toaster]:shadow-lg rounded-3xl',
          description: 'group-[.toast]:text-gray-200',
          actionButton:
            'group-[.toast]:bg-primary group-[.toast]:text-primary-500',
          cancelButton:
            'group-[.toast]:bg-muted group-[.toast]:text-primary-500',
        },
      }}
      {...props}
    />
  );
};

export { Toaster };
