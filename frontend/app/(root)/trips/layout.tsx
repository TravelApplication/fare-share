import { TripContextProvider } from '@/context/TripContext';

export default function TripLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return <TripContextProvider>{children}</TripContextProvider>;
}
