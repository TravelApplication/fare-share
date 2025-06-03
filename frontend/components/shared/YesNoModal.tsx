'use client';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog';
import { X } from 'lucide-react';

export default function YesNoModal({
  title,
  description,
  cancelName = 'Cancel',
  actionName = 'Confirm',
  onConfirm,
  trigger,
}: {
  title: string;
  description: string;
  cancelName?: string;
  actionName?: string;
  onConfirm: () => void;
  trigger: React.ReactNode;
}) {
  return (
    <AlertDialog>
      <AlertDialogTrigger asChild>{trigger}</AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          <div className="flex justify-between">
            <AlertDialogTitle>{title}</AlertDialogTitle>
            <AlertDialogCancel className="group flex items-start bg-transparent border-none p-0 m-0 shadow-none hover:bg-transparent focus:outline-none focus:ring-0 !cursor-pointer">
              <X className="!text-gray-400 !w-6 !h-6 !transition-colors group-hover:!text-gray-500" />
            </AlertDialogCancel>
          </div>
          <AlertDialogDescription>{description}</AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel className="border text-primary-500 border-primary-500 hover:text-light-1 hover:bg-primary-700">
            {cancelName}
          </AlertDialogCancel>
          <AlertDialogAction
            className="bg-primary-600 text-light-1 hover:bg-primary-700"
            onClick={onConfirm}
          >
            {actionName}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
